package com.institute.service;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import com.institute.model.Batch;
import com.institute.model.Faculty;
import com.institute.model.Student;
import com.institute.model.StudentMark;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class ReportService {

    public byte[] generatePdf(Map<String, Object> data) throws Exception {

        // SAFE EXTRACTION
        Student student = (Student) data.getOrDefault("student", null);
        Batch batch = (Batch) data.getOrDefault("batch", null);
        Faculty faculty = (Faculty) data.getOrDefault("faculty", null);

        List<Batch> allBatches = (List<Batch>) data.getOrDefault("allBatches", Collections.emptyList());

        // SAFE batchCodes / names (avoid NPE or "null")
        String batchCodes = allBatches == null || allBatches.isEmpty() ? "-" :
                allBatches.stream()
                        .filter(Objects::nonNull)
                        .map(b -> safe(b.getBatchCode()))
                        .filter(s -> !"-".equals(s))
                        .collect(Collectors.joining(", "));
        if (batchCodes == null || batchCodes.trim().isEmpty()) batchCodes = "-";

        String batchNames = allBatches == null || allBatches.isEmpty() ? "-" :
                allBatches.stream()
                        .filter(Objects::nonNull)
                        .map(b -> safe(b.getBatchName()))
                        .filter(s -> !"-".equals(s))
                        .collect(Collectors.joining(", "));
        if (batchNames == null || batchNames.trim().isEmpty()) batchNames = "-";

        List<StudentMark> marks =
                (List<StudentMark>) data.getOrDefault("testMarks", Collections.emptyList());

        List<Map<String, Object>> projectRecords =
                (List<Map<String, Object>>) data.getOrDefault("projectRecords", Collections.emptyList());

        List<Map<String, Object>> attendanceList =
                (List<Map<String, Object>>) data.getOrDefault("attendanceDetails", Collections.emptyList());

        int totalLectures = ((Number) data.getOrDefault("totalLectures", 0)).intValue();
        int presentLectures = ((Number) data.getOrDefault("presentLectures", 0)).intValue();
        int absentLectures = ((Number) data.getOrDefault("absentLectures", 0)).intValue();

        double attendancePercentage =
                Double.parseDouble(String.valueOf(data.getOrDefault("attendancePercentage", 0.0)));

        double totalFees = Double.parseDouble(String.valueOf(data.getOrDefault("totalFees", 0.0)));
        double paidAmount = Double.parseDouble(String.valueOf(data.getOrDefault("paidAmount", 0.0)));
        double pendingAmount = Double.parseDouble(String.valueOf(data.getOrDefault("pendingAmount", 0.0)));
        double downPayment = Double.parseDouble(String.valueOf(data.getOrDefault("downPayment", 0.0)));

        String lastPresentDate = safe(data.getOrDefault("lastPresentDate", "N/A"));

        // PDF SETUP
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Document document = new Document(PageSize.A4.rotate(), 36, 36, 70, 50);
        PdfWriter writer = PdfWriter.getInstance(document, out);
        writer.setPageEvent(new HeaderFooter());
        document.open();

        // FONTS
        Font bigWhite = new Font(Font.FontFamily.HELVETICA, 20, Font.BOLD, BaseColor.WHITE);
        Font smallWhite = new Font(Font.FontFamily.HELVETICA, 11, Font.NORMAL, BaseColor.WHITE);
        Font sectionFont = new Font(Font.FontFamily.HELVETICA, 13, Font.BOLD);
        Font normalFont = new Font(Font.FontFamily.HELVETICA, 10);

        // ================================
        // HEADER
        // ================================
        PdfPTable header = new PdfPTable(3);
        header.setWidthPercentage(100);
        header.setWidths(new int[]{1, 3, 1});

        BaseColor black = BaseColor.BLACK;

        PdfPCell logoCell = new PdfPCell();
        logoCell.setBackgroundColor(black);
        logoCell.setBorder(Rectangle.NO_BORDER);

        try {
            InputStream is = getClass().getResourceAsStream("/static/logo.png");
            if (is != null) {
                Image logo = Image.getInstance(is.readAllBytes());
                logo.scaleToFit(120, 60);
                logoCell.addElement(logo);
            }
        } catch (Exception ignored) {}

        header.addCell(logoCell);

        PdfPCell center = new PdfPCell();
        center.setBackgroundColor(black);
        center.setBorder(Rectangle.NO_BORDER);

        Paragraph t1 = new Paragraph("SPARK IT INSTITUTE", bigWhite);
        t1.setAlignment(Element.ALIGN_CENTER);
        Paragraph t2 = new Paragraph("Training for successful career", smallWhite);
        t2.setAlignment(Element.ALIGN_CENTER);

        center.addElement(t1);
        center.addElement(t2);
        header.addCell(center);

        PdfPCell right = new PdfPCell();
        right.setBackgroundColor(black);
        right.setBorder(Rectangle.NO_BORDER);
        header.addCell(right);

        header.setSpacingAfter(25f);
        document.add(header);

        // ================================
        // STUDENT INFORMATION
        // ================================
        PdfPTable info = new PdfPTable(2);
        info.setWidthPercentage(100);
        info.setWidths(new int[]{3, 3});

        addRow(info, "Student Name", safe(student != null ? student.getName() : null), normalFont);
        addRow(info, "Mobile", safe(student != null ? student.getMobile() : null), normalFont);
        addRow(info, "Email", safe(student != null ? student.getEmail() : null), normalFont);
        addRow(info, "Course", safe(student != null ? student.getCourseEnrolledFor() : null), normalFont);
        addRow(info, "Admission", safe(student != null ? student.getAdmissionDate() : null), normalFont);

        addRow(info, "Batch Code", batchCodes, normalFont);
        addRow(info, "Batch Name", batchNames, normalFont);

        addRow(info, "Faculty", safe(faculty != null ? faculty.getName() : null), normalFont);

        document.add(info);
        document.add(Chunk.NEWLINE);

        // ================================
        // ATTENDANCE SUMMARY
        // ================================
        document.add(new Paragraph("Attendance Summary", sectionFont));
        document.add(new Paragraph("Total Lectures: " + totalLectures, normalFont));
        document.add(new Paragraph("Present: " + presentLectures, normalFont));
        document.add(new Paragraph("Absent: " + absentLectures, normalFont));
        document.add(new Paragraph("Attendance %: " + attendancePercentage, normalFont));
        document.add(new Paragraph("Last Present Date: " + lastPresentDate, normalFont));
        document.add(Chunk.NEWLINE);

        // ================================
        // FEES SUMMARY
        // ================================
        document.add(new Paragraph("Fees Summary", sectionFont));
        document.add(new Paragraph("Total Fees: ₹" + safeNumber(totalFees), normalFont));
        document.add(new Paragraph("Down Payment: ₹" + safeNumber(downPayment), normalFont));
        document.add(new Paragraph("Paid: ₹" + safeNumber(paidAmount), normalFont));
        document.add(new Paragraph("Pending: ₹" + safeNumber(pendingAmount), normalFont));
        document.add(Chunk.NEWLINE);

        // ================================
        // TEST TABLE
        // ================================
        Paragraph testHead = new Paragraph("TEST RECORDS", sectionFont);
        testHead.setAlignment(Element.ALIGN_CENTER);
        testHead.setSpacingBefore(10f);
        testHead.setSpacingAfter(8f);
        document.add(testHead);

        PdfPTable testTable = new PdfPTable(4);
        testTable.setWidthPercentage(100);

        testTable.addCell(head("Test"));
        testTable.addCell(head("Date"));
        testTable.addCell(head("Marks"));
        testTable.addCell(head("Grade"));

        if (marks == null || marks.isEmpty()) {
            PdfPCell nd = new PdfPCell(new Phrase("No Test Records"));
            nd.setColspan(4);
            testTable.addCell(nd);
        } else {
            for (StudentMark m : marks) {
                String title = m != null && m.getTest() != null ? safe(m.getTest().getTitle()) : "-";
                String date = m != null && m.getTest() != null ? safe(m.getTest().getTestDate()) : "-";
                String marksStr = m != null ? safe(m.getMarks()) : "-";
                String grade = m != null ? safe(m.getGrade()) : "-";

                testTable.addCell(cell(title));
                testTable.addCell(cell(date));
                testTable.addCell(cell(marksStr));
                testTable.addCell(cell(grade));
            }
        }

        document.add(testTable);
        document.add(Chunk.NEWLINE);

        // ================================
        // PROJECT TABLE
        // ================================
        Paragraph projHead = new Paragraph("PROJECT RECORDS", sectionFont);
        projHead.setAlignment(Element.ALIGN_CENTER);
        projHead.setSpacingBefore(10f);
        projHead.setSpacingAfter(8f);
        document.add(projHead);

        PdfPTable proj = new PdfPTable(5);
        proj.setWidthPercentage(100);

        proj.addCell(head("Project Topic"));
        proj.addCell(head("Technology"));
        proj.addCell(head("Status"));
        proj.addCell(head("Assigned"));
        proj.addCell(head("Completed"));

        if (projectRecords == null || projectRecords.isEmpty()) {
            PdfPCell nd = new PdfPCell(new Phrase("No Project Records"));
            nd.setColspan(5);
            proj.addCell(nd);
        } else {
            for (Map<String, Object> p : projectRecords) {
                proj.addCell(cell(safe(p.get("projectTopic"))));
                proj.addCell(cell(safe(p.get("technology"))));
                proj.addCell(cell(safe(p.get("status"))));
                proj.addCell(cell(safe(p.get("assignedDate"))));
                proj.addCell(cell(safe(p.get("completedDate"))));
            }
        }

        document.add(proj);
        document.add(Chunk.NEWLINE);

        // ================================
        // ATTENDANCE DETAIL TABLE
        // ================================
        Paragraph attDetailsHead = new Paragraph("ATTENDANCE DETAILS", sectionFont);
        attDetailsHead.setAlignment(Element.ALIGN_CENTER);
        attDetailsHead.setSpacingBefore(10f);
        attDetailsHead.setSpacingAfter(8f);
        document.add(attDetailsHead);

        PdfPTable att = new PdfPTable(5);
        att.setWidthPercentage(100);

        att.addCell(head("Date"));
        att.addCell(head("Batch"));
        att.addCell(head("Topic"));
        att.addCell(head("Faculty"));
        att.addCell(head("Status"));

        if (attendanceList == null || attendanceList.isEmpty()) {
            PdfPCell nd = new PdfPCell(new Phrase("No Attendance Records"));
            nd.setColspan(5);
            att.addCell(nd);
        } else {
            for (Map<String, Object> a : attendanceList) {
                String date = safe(a.get("date"));
                String bcode = safe(a.get("batchCode"));
                String topic = safe(a.get("topic"));
                String facName = safe(a.get("facultyName"));
                String facCode = safe(a.get("facultyCode"));
                String status = safe(a.get("status"));

                att.addCell(cell(date));
                att.addCell(cell(bcode));
                att.addCell(cell(topic));
                att.addCell(cell(facName + " (" + facCode + ")"));
                att.addCell(cell(status));
            }
        }

        document.add(att);
        document.close();

        return out.toByteArray();
    }

    // ================================
    // HELPERS
    // ================================
    private String safe(Object o) {
        if (o == null) return "-";
        String s = String.valueOf(o).trim();
        return s.isEmpty() ? "-" : s;
    }

    private String safeNumber(double d) {
        // show integer if whole, else show with 2 decimals
        if (Double.isNaN(d)) return "0";
        if (d == (long) d) return String.valueOf((long) d);
        return String.format("%.2f", d);
    }

    private void addRow(PdfPTable t, String key, String value, Font f) {
        t.addCell(new Phrase(key));
        t.addCell(new Phrase(value == null ? "-" : value, f));
    }

    private PdfPCell head(String text) {
        PdfPCell c = new PdfPCell(new Phrase(text, new Font(Font.FontFamily.HELVETICA, 11, Font.BOLD)));
        c.setBackgroundColor(BaseColor.LIGHT_GRAY);
        c.setPadding(6);
        return c;
    }

    private PdfPCell cell(String text) {
        PdfPCell c = new PdfPCell(new Phrase(text == null ? "-" : text));
        c.setPadding(6);
        return c;
    }

    // ================================
    // FOOTER
    // ================================
    private static class HeaderFooter extends PdfPageEventHelper {
        Font f = new Font(Font.FontFamily.HELVETICA, 9, Font.ITALIC);

        @Override
        public void onEndPage(PdfWriter writer, Document doc) {

            PdfPTable footer = new PdfPTable(2);
            try {
                footer.setWidthPercentage(100);
                footer.setWidths(new int[]{8, 1});

                footer.addCell(make("Generated by Spark Institute ERP System", f, Rectangle.TOP, Element.ALIGN_LEFT));
                footer.addCell(make("Page " + writer.getPageNumber(), f, Rectangle.TOP, Element.ALIGN_RIGHT));

                footer.writeSelectedRows(0, -1,
                        doc.leftMargin(),
                        doc.bottomMargin() - 5,
                        writer.getDirectContent());
            } catch (Exception ignored) {}
        }

        private PdfPCell make(String text, Font f, int border, int align) {
            PdfPCell c = new PdfPCell(new Phrase(text, f));
            c.setBorder(border);
            c.setHorizontalAlignment(align);
            return c;
        }
    }
}
