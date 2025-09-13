package com.chat.sr.controller;

import com.chat.sr.model.User;
import com.chat.sr.service.UserService;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;


@RestController
public class CommonAuthController {
    @Autowired
    private UserService userService;

    @GetMapping("/download-pdf")
    public ResponseEntity<byte[]> downloadPdf() {
        try {
            List<User> users = userService.findAllUsers();

            PDDocument document = new PDDocument();
            PDPage page = new PDPage(PDRectangle.A4); // A4 size
            document.addPage(page);

            PDPageContentStream contentStream = new PDPageContentStream(document, page);

            // Draw Logo
            InputStream logoStream = getClass().getResourceAsStream("/static/logo.jpg");
            if (logoStream != null) {
                PDImageXObject logo = PDImageXObject.createFromByteArray(document, logoStream.readAllBytes(), "logo");
                contentStream.drawImage(logo, 50, 770, 60, 60);
            }

            // Header Text (Company Info)
            contentStream.beginText();
            contentStream.setFont(PDType1Font.HELVETICA_BOLD, 12);
            contentStream.newLineAtOffset(400, 800);
            contentStream.showText("Vetina Limited");
            contentStream.newLineAtOffset(0, -15);
            contentStream.setFont(PDType1Font.HELVETICA, 10);
            contentStream.showText("123 Farm Tech Road");
            contentStream.newLineAtOffset(0, -12);
            contentStream.showText("Dhaka, Bangladesh");
            contentStream.newLineAtOffset(0, -12);
            contentStream.showText("Phone: +8801XXXXXXXXX");
            contentStream.endText();

            // Draw line BELOW the header (e.g. at y=750)
            contentStream.moveTo(40, 750);
            contentStream.lineTo(560, 750);
            contentStream.setStrokingColor(0, 0, 0);  // black line
            contentStream.setLineWidth(1);
            contentStream.stroke();

            contentStream.beginText();
            contentStream.setFont(PDType1Font.HELVETICA_OBLIQUE, 12);
            contentStream.newLineAtOffset(409, 735);

            LocalDateTime now = LocalDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm a");
            String formattedDateTime = now.format(formatter);

            contentStream.showText("Date: " + formattedDateTime);
            contentStream.endText();

            // Title
            contentStream.beginText();
            contentStream.setFont(PDType1Font.HELVETICA_BOLD, 16);
            contentStream.newLineAtOffset(220, 700);
            contentStream.showText("User List Report");
            contentStream.endText();

            float margin = 50;
            float yStart = 660;  // টেবিল শুরু হওয়ার y পজিশন
            int cols = 5;
            float[] colWidths = {40, 120, 150, 100, 80}; // ৫টি কলামের width
            float tableWidth = 0;
            for (float w : colWidths) {
                tableWidth += w;
            }
            float rowHeight = 20;
            float tableBottomY = 100;
            float tableX = margin;

            // Draw header row background and text
            contentStream.setNonStrokingColor(200, 200, 200); // হালকা ধূসর ব্যাকগ্রাউন্ড
            contentStream.addRect(tableX, yStart, tableWidth, rowHeight);
            contentStream.fill();
            contentStream.setNonStrokingColor(0, 0, 0); // কালো ফন্ট

            // Draw header text
            contentStream.beginText();
            contentStream.setFont(PDType1Font.HELVETICA_BOLD, 12);
            float textx = tableX + 5;
            float texty = yStart + 5;
            contentStream.newLineAtOffset(textx, texty);
            contentStream.showText("S/N");
            contentStream.newLineAtOffset(colWidths[0], 0);
            contentStream.showText("Name");
            contentStream.newLineAtOffset(colWidths[1], 0);
            contentStream.showText("Email");
            contentStream.newLineAtOffset(colWidths[2], 0);
            contentStream.showText("Phone");
            contentStream.newLineAtOffset(colWidths[3], 0);
            contentStream.showText("District");
            contentStream.endText();

            // Draw header row border
            contentStream.setStrokingColor(0, 0, 0);
            contentStream.addRect(tableX, yStart, tableWidth, rowHeight);
            contentStream.stroke();

            float nextY = yStart - rowHeight;
            int serial = 1;

            for (User user : users) {
                if (nextY < tableBottomY) {
                    // এখানে পেজ ব্রেক হ্যান্ডেল করতে পারেন যদি অনেক ডাটা থাকে
                    break;
                }

                // Draw row border
                contentStream.addRect(tableX, nextY, tableWidth, rowHeight);
                contentStream.stroke();

                // Draw vertical lines for columns
                float nextX = tableX;
                for (int i = 0; i < cols; i++) {
                    contentStream.moveTo(nextX, nextY);
                    contentStream.lineTo(nextX, nextY + rowHeight);
                    contentStream.stroke();
                    nextX += colWidths[i];
                }
                // Last vertical line on right side
                contentStream.moveTo(nextX, nextY);
                contentStream.lineTo(nextX, nextY + rowHeight);
                contentStream.stroke();

                // Write text in the row with null-safe strings
                contentStream.beginText();
                contentStream.setFont(PDType1Font.HELVETICA, 12);
                contentStream.newLineAtOffset(tableX + 5, nextY + 5);
                contentStream.showText(String.valueOf(serial++));
                contentStream.newLineAtOffset(colWidths[0], 0);
                contentStream.showText(safeString(user.getUserName()));
                contentStream.newLineAtOffset(colWidths[1], 0);
                contentStream.showText(safeString(user.getEmail()));
                contentStream.newLineAtOffset(colWidths[2], 0);
                contentStream.showText(safeString(user.getPhone()));
                contentStream.newLineAtOffset(colWidths[3], 0);
                contentStream.showText(safeString(user.getDistrict()));
                contentStream.endText();

                nextY -= rowHeight;
            }

            // Draw line ABOVE the footer (e.g. at y=72)
            contentStream.moveTo(50, 72);
            contentStream.lineTo(550, 72);
            contentStream.setStrokingColor(0, 0, 0);
            contentStream.setLineWidth(1);
            contentStream.stroke();

            // Footer
            contentStream.beginText();
            contentStream.setFont(PDType1Font.HELVETICA_OBLIQUE, 10);
            contentStream.newLineAtOffset(270, 30);
            contentStream.showText("Page 1");
            contentStream.endText();

            contentStream.close();

            // Export to byte[]
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            document.save(baos);
            document.close();

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=users.pdf")
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(baos.toByteArray());

        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    // Null-safe helper method
    private String safeString(String text) {
        return text == null ? "" : text;
    }

}
