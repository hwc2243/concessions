package com.concessions.local.service;

import javax.swing.ImageIcon;

public class QRGeneratorService {

	public QRGeneratorService() {
		// TODO Auto-generated constructor stub
	}

	public ImageIcon generateQRCode(String content, int width, int height) {
		ImageIcon qrIcon = null;
		
		 try {
	            com.google.zxing.qrcode.QRCodeWriter qrCodeWriter = new com.google.zxing.qrcode.QRCodeWriter();
	            com.google.zxing.common.BitMatrix bitMatrix = qrCodeWriter.encode(
	                content, 
	                com.google.zxing.BarcodeFormat.QR_CODE, 
	                width, 
	                height
	            );
	            
	            // Convert BitMatrix to BufferedImage
	            int matrixWidth = bitMatrix.getWidth();
	            java.awt.image.BufferedImage image = new java.awt.image.BufferedImage(matrixWidth, matrixWidth, java.awt.image.BufferedImage.TYPE_INT_RGB);
	            image.createGraphics();

	            java.awt.Graphics2D graphics = (java.awt.Graphics2D) image.getGraphics();
	            graphics.setColor(java.awt.Color.WHITE);
	            graphics.fillRect(0, 0, matrixWidth, matrixWidth);
	            graphics.setColor(java.awt.Color.BLACK);

	            for (int i = 0; i < matrixWidth; i++) {
	                for (int j = 0; j < matrixWidth; j++) {
	                    if (bitMatrix.get(i, j)) {
	                        graphics.fillRect(i, j, 1, 1);
	                    }
	                }
	            }
	            
	            // Convert BufferedImage to ImageIcon for display in JLabel
	            qrIcon = new ImageIcon(image);

	        } catch (Exception e) {
	            e.printStackTrace();
	        }		
		return qrIcon;
	}
}
