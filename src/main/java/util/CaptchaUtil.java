package util;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.util.Random;

public class CaptchaUtil {
    private static String currentCaptcha;

    public static String generateCaptcha(Canvas canvas) {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder sb = new StringBuilder();
        Random random = new Random();
        
        for (int i = 0; i < 5; i++) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }
        
        currentCaptcha = sb.toString();
        drawCaptcha(canvas, currentCaptcha);
        return currentCaptcha;
    }

    private static void drawCaptcha(Canvas canvas, String code) {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        double width = canvas.getWidth();
        double height = canvas.getHeight();
        
        // Background
        gc.setFill(Color.web("#f1f2f6"));
        gc.fillRect(0, 0, width, height);
        
        // Noise lines
        Random random = new Random();
        gc.setLineWidth(1);
        for (int i = 0; i < 10; i++) {
            gc.setStroke(Color.rgb(random.nextInt(255), random.nextInt(255), random.nextInt(255), 0.5));
            gc.strokeLine(random.nextDouble() * width, random.nextDouble() * height, 
                          random.nextDouble() * width, random.nextDouble() * height);
        }

        // Draw text
        gc.setFont(Font.font("Courier New", FontWeight.BOLD, 24));
        for (int i = 0; i < code.length(); i++) {
            gc.setFill(Color.rgb(random.nextInt(150), random.nextInt(150), random.nextInt(150)));
            gc.save();
            gc.translate(20 + i * 30, height / 2 + 10);
            gc.rotate(random.nextInt(40) - 20);
            gc.fillText(String.valueOf(code.charAt(i)), 0, 0);
            gc.restore();
        }
    }

    public static boolean validate(String input) {
        return currentCaptcha != null && currentCaptcha.equalsIgnoreCase(input);
    }
}
