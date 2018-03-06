package com.filelug.desktop;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.EnumMap;
import java.util.Map;

public class QRCodeSwingDemo {

    // Tutorial: http://zxing.github.io/zxing/apidocs/index.html

    public static void main(String[] args) {
        String myCodeText = "http://www.filelug.com";
//        String filePath = "/Users/masonhsieh/Downloads/temp/QRCodeDemo.png";
        int barcodeSize = 250;
        int frameSize = 400;
//        String fileType = "png";
//        File myFile = new File(filePath);
        try {

            Map<EncodeHintType, Object> hintMap = new EnumMap<EncodeHintType, Object>(EncodeHintType.class);
            hintMap.put(EncodeHintType.CHARACTER_SET, "UTF-8");

            // Now with zxing version 3.2.1 you could change border size (white border size to just 1)
            hintMap.put(EncodeHintType.MARGIN, 4); /* default = 4 */
            hintMap.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.L);

            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            BitMatrix bitMatrix = qrCodeWriter.encode(myCodeText, BarcodeFormat.QR_CODE, barcodeSize, barcodeSize, hintMap);
            int imageWidth = bitMatrix.getWidth();
            BufferedImage image = new BufferedImage(imageWidth, imageWidth, BufferedImage.TYPE_INT_RGB);
            image.createGraphics();

            Graphics2D graphics = (Graphics2D) image.getGraphics();
            graphics.setColor(Color.WHITE);
            graphics.fillRect(0, 0, imageWidth, imageWidth);
            graphics.setColor(Color.BLACK);

            for (int i = 0; i < imageWidth; i++) {
                for (int j = 0; j < imageWidth; j++) {
                    if (bitMatrix.get(i, j)) {
                        graphics.fillRect(i, j, 1, 1);
                    }
                }
            }

            final JFrame frame = new JFrame("Scan with your Filelug APP.");

            frame.getContentPane().setLayout(new BorderLayout());

            final JLabel qrCodeLabel = new JLabel(new ImageIcon(image));

            frame.getContentPane().add(qrCodeLabel, BorderLayout.CENTER);

            frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

            Dimension frameDimension = new Dimension(frameSize, frameSize);
            frame.setPreferredSize(frameDimension);
            frame.setSize(frameDimension);
//            frame.pack();

            frame.setVisible(true);

//            ImageIO.write(image, fileType, myFile);
        } catch (Exception e) {
            e.printStackTrace();
        }

//        System.out.println("\n\nYou have successfully created QR Code.");
    }
}