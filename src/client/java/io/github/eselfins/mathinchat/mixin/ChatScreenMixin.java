package io.github.eselfins.mathinchat.mixin;

import io.github.eselfins.mathinchat.MathExpression;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.ChatScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ChatScreen.class)
public abstract class ChatScreenMixin {
	@Shadow
	protected EditBox input;

	@Inject(method = "extractRenderState", at = @At("TAIL"))
	private void mathinchat$renderGhost(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float delta, CallbackInfo ci) {
		String text = input.getValue();
		if (text.isBlank() || text.startsWith("/")) {
			return;
		}

		Double result = MathExpression.tryEvaluate(text);
		if (result == null) {
			return;
		}

		String ghost = "= " + MathExpression.formatGhost(text, result);
		Font font = Minecraft.getInstance().font;
		int screenWidth = ((net.minecraft.client.gui.screens.Screen) (Object) this).width;

		int x = input.getX() + (input.isBordered() ? 4 : 0) + font.width(text);
		int y = input.isBordered() ? input.getY() + (input.getHeight() - 8) / 2 : input.getY();
		x = Math.min(x, screenWidth - 4 - font.width(ghost));

		graphics.text(font, ghost, x, y, 0xFFAAAAAA, true);
	}
}
