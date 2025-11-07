package com.midoribank.atm.utils;

import javafx.animation.*;
import javafx.scene.Node;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.effect.DropShadow;
import javafx.scene.control.Button;
import javafx.scene.Cursor;
import javafx.scene.paint.Color;
import javafx.util.Duration;

public class AnimationUtils {

    public static void fadeIn(Node node, double duration) {
        FadeTransition ft = new FadeTransition(Duration.millis(duration), node);
        ft.setFromValue(0.0);
        ft.setToValue(1.0);
        ft.play();
    }

    public static void fadeOut(Node node, double duration) {
        FadeTransition ft = new FadeTransition(Duration.millis(duration), node);
        ft.setFromValue(1.0);
        ft.setToValue(0.0);
        ft.play();
    }

    public static void slideInFromRight(Node node, double duration) {
        double originalX = node.getLayoutX();
        node.setLayoutX(originalX + 300);
        node.setOpacity(0);
        
        TranslateTransition tt = new TranslateTransition(Duration.millis(duration), node);
        tt.setFromX(300);
        tt.setToX(0);
        
        FadeTransition ft = new FadeTransition(Duration.millis(duration), node);
        ft.setFromValue(0.0);
        ft.setToValue(1.0);
        
        ParallelTransition pt = new ParallelTransition(tt, ft);
        pt.play();
    }

    public static void slideInFromLeft(Node node, double duration) {
        TranslateTransition tt = new TranslateTransition(Duration.millis(duration), node);
        tt.setFromX(-300);
        tt.setToX(0);
        
        FadeTransition ft = new FadeTransition(Duration.millis(duration), node);
        ft.setFromValue(0.0);
        ft.setToValue(1.0);
        
        ParallelTransition pt = new ParallelTransition(tt, ft);
        pt.play();
    }

    public static void slideInFromTop(Node node, double duration) {
        TranslateTransition tt = new TranslateTransition(Duration.millis(duration), node);
        tt.setFromY(-200);
        tt.setToY(0);
        
        FadeTransition ft = new FadeTransition(Duration.millis(duration), node);
        ft.setFromValue(0.0);
        ft.setToValue(1.0);
        
        ParallelTransition pt = new ParallelTransition(tt, ft);
        pt.play();
    }

    public static void slideInFromBottom(Node node, double duration) {
        TranslateTransition tt = new TranslateTransition(Duration.millis(duration), node);
        tt.setFromY(200);
        tt.setToY(0);
        
        FadeTransition ft = new FadeTransition(Duration.millis(duration), node);
        ft.setFromValue(0.0);
        ft.setToValue(1.0);
        
        ParallelTransition pt = new ParallelTransition(tt, ft);
        pt.play();
    }

    public static void scaleIn(Node node, double duration) {
        ScaleTransition st = new ScaleTransition(Duration.millis(duration), node);
        st.setFromX(0.0);
        st.setFromY(0.0);
        st.setToX(1.0);
        st.setToY(1.0);
        st.setInterpolator(Interpolator.EASE_OUT);
        
        FadeTransition ft = new FadeTransition(Duration.millis(duration), node);
        ft.setFromValue(0.0);
        ft.setToValue(1.0);
        
        ParallelTransition pt = new ParallelTransition(st, ft);
        pt.play();
    }

    public static void shake(Node node) {
        TranslateTransition tt = new TranslateTransition(Duration.millis(70), node);
        tt.setFromX(0);
        tt.setByX(10);
        tt.setCycleCount(4);
        tt.setAutoReverse(true);
        tt.play();
    }

    public static void pulse(Node node) {
        ScaleTransition st = new ScaleTransition(Duration.millis(200), node);
        st.setFromX(1.0);
        st.setFromY(1.0);
        st.setToX(1.1);
        st.setToY(1.1);
        st.setCycleCount(2);
        st.setAutoReverse(true);
        st.play();
    }

    public static void bounce(Node node) {
        TranslateTransition tt = new TranslateTransition(Duration.millis(500), node);
        tt.setFromY(0);
        tt.setByY(-20);
        tt.setCycleCount(2);
        tt.setAutoReverse(true);
        tt.setInterpolator(Interpolator.EASE_BOTH);
        tt.play();
    }

    public static void glowEffect(Node node, Color color, double radius) {
        DropShadow glow = new DropShadow();
        glow.setColor(color);
        glow.setRadius(radius);
        glow.setSpread(0.6);
        
        Timeline timeline = new Timeline(
            new KeyFrame(Duration.ZERO, new KeyValue(glow.radiusProperty(), 0)),
            new KeyFrame(Duration.millis(300), new KeyValue(glow.radiusProperty(), radius))
        );
        
        node.setEffect(glow);
        timeline.play();
    }

    public static void removeEffect(Node node, double duration) {
        FadeTransition ft = new FadeTransition(Duration.millis(duration), node);
        ft.setFromValue(1.0);
        ft.setToValue(1.0);
        ft.setOnFinished(e -> node.setEffect(null));
        ft.play();
    }

    public static void staggeredFadeIn(Node[] nodes, double delayBetween, double duration) {
        for (int i = 0; i < nodes.length; i++) {
            final Node node = nodes[i];
            node.setOpacity(0);
            
            PauseTransition pause = new PauseTransition(Duration.millis(delayBetween * i));
            pause.setOnFinished(e -> fadeIn(node, duration));
            pause.play();
        }
    }

    public static void successAnimation(Node node) {
        ScaleTransition st = new ScaleTransition(Duration.millis(150), node);
        st.setToX(1.15);
        st.setToY(1.15);
        st.setCycleCount(2);
        st.setAutoReverse(true);
        
        glowEffect(node, Color.web("#14FF00"), 15);
        
        st.setOnFinished(e -> {
            Timeline removeGlow = new Timeline(
                new KeyFrame(Duration.millis(500), evt -> node.setEffect(null))
            );
            removeGlow.play();
        });
        
        st.play();
    }

    public static void errorAnimation(Node node) {
        shake(node);
        glowEffect(node, Color.web("#FF0000"), 15);
        
        Timeline removeGlow = new Timeline(
            new KeyFrame(Duration.millis(500), e -> node.setEffect(null))
        );
        removeGlow.play();
    }

    public static void buttonClickAnimation(Node node) {
        ScaleTransition st = new ScaleTransition(Duration.millis(100), node);
        st.setToX(0.95);
        st.setToY(0.95);
        st.setCycleCount(2);
        st.setAutoReverse(true);
        st.play();
    }

    public static void smoothTransition(Node fromNode, Node toNode, double duration) {
        fadeOut(fromNode, duration / 2);
        PauseTransition pause = new PauseTransition(Duration.millis(duration / 2));
        pause.setOnFinished(e -> fadeIn(toNode, duration / 2));
        pause.play();
    }

    public static void rotateIn(Node node, double duration) {
        RotateTransition rt = new RotateTransition(Duration.millis(duration), node);
        rt.setFromAngle(180);
        rt.setToAngle(0);
        
        FadeTransition ft = new FadeTransition(Duration.millis(duration), node);
        ft.setFromValue(0.0);
        ft.setToValue(1.0);
        
        ParallelTransition pt = new ParallelTransition(rt, ft);
        pt.play();
    }

    public static void flipIn(Node node, double duration) {
        RotateTransition rt = new RotateTransition(Duration.millis(duration), node);
        rt.setAxis(javafx.geometry.Point3D.ZERO.add(0, 1, 0));
        rt.setFromAngle(90);
        rt.setToAngle(0);
        rt.setInterpolator(Interpolator.EASE_OUT);
        
        FadeTransition ft = new FadeTransition(Duration.millis(duration), node);
        ft.setFromValue(0.0);
        ft.setToValue(1.0);
        
        ParallelTransition pt = new ParallelTransition(rt, ft);
        pt.play();
    }
    public static void setupButtonHoverEffects(Button button) {
        if (button != null) {
            ColorAdjust hoverEffect = new ColorAdjust(0, 0, -0.1, 0);
            ColorAdjust clickEffect = new ColorAdjust(0, 0, -0.25, 0);

            button.setOnMouseEntered(e -> {
                if (button.getScene() != null) button.getScene().setCursor(Cursor.HAND);
                button.setEffect(hoverEffect);
            });

            button.setOnMouseExited(e -> {
                if (button.getScene() != null) button.getScene().setCursor(Cursor.DEFAULT);
                button.setEffect(null);
            });

            button.setOnMousePressed(e -> button.setEffect(clickEffect));

            button.setOnMouseReleased(e -> {
                if (button.isHover()) button.setEffect(hoverEffect);
                else button.setEffect(null);
            });
        }
    }

    public static void setupNodeHoverEffects(Node node) {
        if (node != null) {
            ColorAdjust hoverEffect = new ColorAdjust(0, 0, -0.1, 0);
            ColorAdjust clickEffect = new ColorAdjust(0, 0, -0.25, 0);

            node.setOnMouseEntered(e -> {
                if (node.getScene() != null) node.getScene().setCursor(Cursor.HAND);
                node.setEffect(hoverEffect);
            });

            node.setOnMouseExited(e -> {
                if (node.getScene() != null) node.getScene().setCursor(Cursor.DEFAULT);
                node.setEffect(null);
            });

            node.setOnMousePressed(e -> node.setEffect(clickEffect));

            node.setOnMouseReleased(e -> {
                if (node.isHover()) node.setEffect(hoverEffect);
                else node.setEffect(null);
            });
        }
    }
}
