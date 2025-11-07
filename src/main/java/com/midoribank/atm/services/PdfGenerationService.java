package com.midoribank.atm.services;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.Image;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import com.midoribank.atm.App;
import com.midoribank.atm.models.Movimentacao;
import com.midoribank.atm.models.UserProfile;
import java.awt.Color;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class PdfGenerationService {

    private static final Color COLOR_MIDORI_GREY = new Color(115, 115, 115);

    private static final Font FONT_TITLE = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 20, Color.BLACK);
    private static final Font FONT_HEADER = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, Color.BLACK);
    private static final Font FONT_BODY = FontFactory.getFont(FontFactory.HELVETICA, 10, COLOR_MIDORI_GREY);
    private static final Font FONT_FOOTER = FontFactory.getFont(FontFactory.HELVETICA, 8, Color.GRAY);
    private static final Font FONT_SALDO = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, Color.BLACK);

    public boolean gerarPdf(UserProfile user, List<Movimentacao> movimentacoes, String filePath) {
        Document document = new Document(PageSize.A4);
        try {
            PdfWriter.getInstance(document, new FileOutputStream(filePath));
            document.open();

            addHeader(document);
            addUserInfo(document, user);
            addMovimentacoesTable(document, movimentacoes);
            addFooter(document);

            document.close();
            return true;

        } catch (DocumentException | IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    private void addHeader(Document document) throws DocumentException, IOException {
        PdfPTable headerTable = new PdfPTable(2);
        headerTable.setWidthPercentage(100);
        headerTable.setWidths(new float[]{1, 4});
        headerTable.getDefaultCell().setBorder(PdfPCell.NO_BORDER);

        try (InputStream is = App.class.getResourceAsStream("/com/midoribank/atm/splash/LogoIcon.png")) {
            if (is != null) {
                byte[] bytes = is.readAllBytes();
                Image logo = Image.getInstance(bytes);
                logo.scaleToFit(50, 50);
                headerTable.addCell(logo);
            } else {
                headerTable.addCell(" ");
            }
        } catch (Exception e) {
            e.printStackTrace();
            headerTable.addCell(" ");
        }

        Paragraph title = new Paragraph("MidoriBank", FONT_TITLE);
        title.getFont().setColor(COLOR_MIDORI_GREY);
        
        Paragraph subtitle = new Paragraph("Extrato Bancário", FONT_HEADER);
        subtitle.getFont().setColor(Color.DARK_GRAY);
        
        PdfPCell titleCell = new PdfPCell();
        titleCell.addElement(title);
        titleCell.addElement(subtitle);
        titleCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        titleCell.setBorder(PdfPCell.NO_BORDER);
        headerTable.addCell(titleCell);

        document.add(headerTable);
        document.add(new Paragraph(" ")); 
    }

    private void addUserInfo(Document document, UserProfile user) throws DocumentException {
        Paragraph info = new Paragraph();
        info.setFont(FONT_BODY); 
        info.add("Cliente: " + user.getNome() + "\n");
        info.add("Agência: " + user.getAgencia() + "   Conta: " + user.getNumeroConta() + "\n\n");
        
        Paragraph saldo = new Paragraph(String.format("Saldo Atual: R$ %.2f", user.getSaldo()), FONT_SALDO);
        
        document.add(info);
        document.add(saldo);
        document.add(new Paragraph(" "));
    }

    private void addMovimentacoesTable(Document document, List<Movimentacao> movimentacoes) throws DocumentException {
        PdfPTable table = new PdfPTable(4);
        table.setWidthPercentage(100);
        table.setWidths(new float[]{2, 4, 2, 2});

        String[] headers = {"Data", "Tipo de Operação", "Valor (R$)", "Tipo"};
        for (String header : headers) {
            PdfPCell cell = new PdfPCell(new Phrase(header, FONT_HEADER));
            cell.setBackgroundColor(Color.LIGHT_GRAY);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cell);
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        
        for (Movimentacao mov : movimentacoes) {
            
            table.addCell(new Phrase(mov.getDataHora().format(formatter), FONT_BODY));
            table.addCell(new Phrase(mov.getTipoMovimentacao().replace("_", " "), FONT_BODY));
            
            String valorStr = String.format("%.2f", mov.getValor());
            boolean isEntrada = mov.getTipoMovimentacao().equals("DEPOSITO") || mov.getTipoMovimentacao().equals("TRANSFERENCIA_RECEBIDA");
            
            PdfPCell valorCell = new PdfPCell(new Phrase(valorStr, FONT_BODY));
            valorCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            table.addCell(valorCell);
            
            PdfPCell tipoCell;
            if (isEntrada) {
                tipoCell = new PdfPCell(new Phrase("Entrada", FONT_BODY));
            } else {
                tipoCell = new PdfPCell(new Phrase("Saída", FONT_BODY));
            }
            tipoCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(tipoCell);
        }

        document.add(table);
    }

    private void addFooter(Document document) throws DocumentException {
        Paragraph footer = new Paragraph(
                "\n\nDocumento gerado automaticamente pelo sistema MidoriBank em " + 
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")),
                FONT_FOOTER
        );
        footer.setAlignment(Element.ALIGN_CENTER);
        document.add(footer);
    }
}