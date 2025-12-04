package com.ajmayen.softwaregazeportal.service;

import com.ajmayen.softwaregazeportal.model.Expense;


import java.util.List;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.Image;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.Rectangle;
import com.lowagie.text.Chunk;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import com.lowagie.text.pdf.draw.LineSeparator;


import org.springframework.stereotype.Service;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.awt.Color;

@Service
public class PdfReportService {

    private final Font TITLE_FONT = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, Color.BLACK);
    private final Font HEADER_FONT = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12,Color.black);
    private final Font DATA_FONT = FontFactory.getFont(FontFactory.HELVETICA, 10, Color.BLACK);
    private final Font BOLD_FONT = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, Color.BLACK);


    public void generateReport(List<Expense> expenses, Document document) throws DocumentException, IOException {

        // --- PAGE 1: SUMMARY REPORT ---

        // 1. Add Company Header (Logo + Text)
        addCompanyHeader(document);

        // 2. Report Title
        Paragraph title = new Paragraph("Expense Report", TITLE_FONT);
        title.setSpacingAfter(20);
        document.add(title);

        // 3. Create Table (5 Columns: Date, Name, Category, Tag, Cost)
        PdfPTable table = new PdfPTable(5);
         table.setWidthPercentage(100);
        table.setWidths(new float[]{2.0f, 3.5f, 2.5f, 1.5f, 2.0f}); // Adjust column widths

        // Add Table Headers
        addTableHeader(table, "Date");
        addTableHeader(table, "Name");
        addTableHeader(table, "Categ" +
                "" +
                "ory");
        addTableHeader(table, "Tag");
        addTableHeader(table, "Cost");

        // Add Data Rows
        Double totalAmount = 0.0;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        for (Expense expense : expenses) {
            table.addCell(new PdfPCell(new Phrase(expense.getDate().format(formatter), DATA_FONT)));
            table.addCell(new PdfPCell(new Phrase(expense.getBillType(), DATA_FONT)));
            table.addCell(new PdfPCell(new Phrase(expense.getTag(), DATA_FONT)));

            // Align Cost to Right
            PdfPCell costCell = new PdfPCell(new Phrase(String.format("%,.2f", expense.getAmount()), DATA_FONT));
            costCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            table.addCell(costCell);

            totalAmount += expense.getAmount();
        }

        // Add Total Row
        PdfPCell totalLabel = new PdfPCell(new Phrase("Total", BOLD_FONT));
        totalLabel.setColspan(4); // Span across first 4 columns
        totalLabel.setHorizontalAlignment(Element.ALIGN_RIGHT);
        totalLabel.setBackgroundColor(new Color(240, 240, 240)); // Light Gray
        table.addCell(totalLabel);

        PdfPCell totalValue = new PdfPCell(new Phrase(String.format("%,.2f", totalAmount), BOLD_FONT));
        totalValue.setHorizontalAlignment(Element.ALIGN_RIGHT);
        totalValue.setBackgroundColor(new Color(240, 240, 240));
        table.addCell(totalValue);

        document.add(table);

        // 4. Add Signatures (Prepared By / Approved By)
        addSignatures(document);

        // --- PAGE 2+: SCREENSHOTS ---

        if (!expenses.isEmpty()) {
            document.newPage(); // Force page break

            Paragraph screenShotTitle = new Paragraph("Screenshots", TITLE_FONT);
            screenShotTitle.setSpacingAfter(20);
            document.add(screenShotTitle);

            for (Expense expense : expenses) {
                // Only process if image exists
                if (expense.getScreenshot() != null && expense.getScreenshot().length > 0) {
                    addScreenshotBlock(document, expense, formatter);
                }
            }
        }
    }

    private void addCompanyHeader(Document document) throws DocumentException, IOException {
        // Load Logo (Assuming logo.png is in src/main/resources/static/ or a known path)
        // If you don't have a file yet, comment out the Image lines
        try {
            Image logo = Image.getInstance("src/main/resources/static/logo.png");
            logo.scaleToFit(100, 50);
            logo.setAlignment(Element.ALIGN_CENTER);
            document.add(logo);
        } catch (Exception e) {
            // Logo not found, ignore
        }

        Paragraph companyName = new Paragraph("Software Gaze Limited", BOLD_FONT);
        companyName.setAlignment(Element.ALIGN_CENTER);
        document.add(companyName);

        Paragraph contact = new Paragraph(
                "Phone: 01988340833 • Website: https://www.softwaregaze.com",
                FontFactory.getFont(FontFactory.HELVETICA, 8)
        );
        contact.setAlignment(Element.ALIGN_CENTER);
        contact.setSpacingAfter(20);
        document.add(contact);
    }

    private void addTableHeader(PdfPTable table, String headerTitle) {
        PdfPCell header = new PdfPCell();
        header.setBackgroundColor(new Color(240, 240, 240)); // Light Gray
        header.setBorderWidth(1);
        header.setPhrase(new Phrase(headerTitle, BOLD_FONT));
        header.setHorizontalAlignment(Element.ALIGN_CENTER);
        header.setVerticalAlignment(Element.ALIGN_MIDDLE);
        header.setPadding(5);
        table.addCell(header);
    }

    private void addSignatures(Document document) throws DocumentException {
        PdfPTable signatureTable = new PdfPTable(2);
        signatureTable.setWidthPercentage(100);
        signatureTable.setSpacingBefore(50);
        signatureTable.setWidths(new float[]{1, 1});

        PdfPCell cell1 = new PdfPCell(new Phrase("Prepared By", DATA_FONT));
        cell1.setBorder(Rectangle.NO_BORDER);
        signatureTable.addCell(cell1);

        PdfPCell cell2 = new PdfPCell(new Phrase("Approved By", DATA_FONT));
        cell2.setBorder(Rectangle.NO_BORDER);
        signatureTable.addCell(cell2);

        // Add Underlines
        PdfPCell lineCell = new PdfPCell(new Phrase("__________________________"));
        lineCell.setBorder(Rectangle.NO_BORDER);
        lineCell.setPaddingTop(30);
        signatureTable.addCell(lineCell);
        signatureTable.addCell(lineCell);

        document.add(signatureTable);
    }

    private void addScreenshotBlock(Document document, Expense expense, DateTimeFormatter formatter) throws DocumentException, IOException {
        // 1. Text Info
        Paragraph name = new Paragraph(expense.getBillType(), BOLD_FONT);
        name.setSpacingBefore(15);
        document.add(name);

        Paragraph date = new Paragraph(
                "Date: " + expense.getDate().format(formatter),
                FontFactory.getFont(FontFactory.HELVETICA, 9)
        );
        date.setSpacingAfter(5);
        document.add(date);

        // 2. The Image
        Image img = Image.getInstance(expense.getScreenshot());
        // Scale image to fit within the page margins (A4 width approx 595pts, minus margins)
        img.scaleToFit(500, 400);
        img.setBorder(Rectangle.BOX);
        img.setBorderColor(Color.LIGHT_GRAY);
        img.setBorderWidth(1);
        document.add(img);

        // 3. Divider
        LineSeparator separator = new LineSeparator();
        separator.setLineColor(Color.LIGHT_GRAY);
        document.add(new Chunk(separator));
    }
}


