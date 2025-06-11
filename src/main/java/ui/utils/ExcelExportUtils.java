package ui.utils;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import domain.menu.Menu;
import domain.menu.SezioneMenu;
import domain.menu.RicettaInMenu;
import domain.menu.CaratteristicaMenu;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Rappresenta una classe di utilità per l'esportazione dei menu in formato Excel nel sistema di catering.
 * Gestisce la conversione dei menu in fogli di calcolo Excel con formattazione professionale.
 */
public class ExcelExportUtils {
    
    /**
     * Esporta un singolo menu in formato Excel con formattazione completa.
     *
     * @param menu     menu da esportare
     * @param filePath percorso del file Excel da creare
     * @throws IOException se si verifica un errore durante la scrittura del file
     */
    public static void esportaMenu(Menu menu, String filePath) throws IOException {
        try (Workbook workbook = new XSSFWorkbook()) {
            
            //Sanifica il nome del foglio per Excel
            String nomeMenuSanificato = sanificaNomeFoglio(menu.getTitolo());
            Sheet sheet = workbook.createSheet("Menu - " + nomeMenuSanificato);
            
            // Stili per l'intestazione
            CellStyle headerStyle = createHeaderStyle(workbook);
            CellStyle titleStyle = createTitleStyle(workbook);
            CellStyle sectionStyle = createSectionStyle(workbook);
            
            int rowNum = 0;
            
            // Titolo del menu
            Row titleRow = sheet.createRow(rowNum++);
            Cell titleCell = titleRow.createCell(0);
            titleCell.setCellValue("MENU: " + menu.getTitolo());
            titleCell.setCellStyle(titleStyle);
            
            // Riga vuota
            rowNum++;
            
            // Informazioni generali del menu
            Row infoHeaderRow = sheet.createRow(rowNum++);
            Cell infoHeaderCell = infoHeaderRow.createCell(0);
            infoHeaderCell.setCellValue("INFORMAZIONI GENERALI");
            infoHeaderCell.setCellStyle(headerStyle);
            
            // ID Menu
            Row idRow = sheet.createRow(rowNum++);
            idRow.createCell(0).setCellValue("ID Menu:");
            idRow.createCell(1).setCellValue(menu.getId());
            
            // Stato
            Row statoRow = sheet.createRow(rowNum++);
            statoRow.createCell(0).setCellValue("Stato:");
            statoRow.createCell(1).setCellValue(menu.getStato());
            
            // Note (se presenti)
            if (menu.getNote() != null && !menu.getNote().trim().isEmpty()) {
                Row noteRow = sheet.createRow(rowNum++);
                noteRow.createCell(0).setCellValue("Note:");
                noteRow.createCell(1).setCellValue(menu.getNote());
            }
            
            // Caratteristiche del menu (se presenti)
            if (!menu.getCaratteristiche().isEmpty()) {
                rowNum++; // Riga vuota
                Row caratteristicheHeaderRow = sheet.createRow(rowNum++);
                Cell caratteristicheHeaderCell = caratteristicheHeaderRow.createCell(0);
                caratteristicheHeaderCell.setCellValue("CARATTERISTICHE");
                caratteristicheHeaderCell.setCellStyle(headerStyle);
                
                for (CaratteristicaMenu caratteristica : menu.getCaratteristiche()) {
                    Row caratteristicaRow = sheet.createRow(rowNum++);
                    caratteristicaRow.createCell(0).setCellValue("• " + caratteristica.getNome());
                }
            }
            
            // Riga vuota prima delle sezioni
            rowNum++;
            
            // Sezioni del menu
            Row sezioniHeaderRow = sheet.createRow(rowNum++);
            Cell sezioniHeaderCell = sezioniHeaderRow.createCell(0);
            sezioniHeaderCell.setCellValue("SEZIONI DEL MENU");
            sezioniHeaderCell.setCellStyle(headerStyle);
            
            // Intestazione tabella ricette
            Row tableHeaderRow = sheet.createRow(rowNum++);
            tableHeaderRow.createCell(0).setCellValue("Sezione");
            tableHeaderRow.createCell(1).setCellValue("Ricetta nel Menu");
            tableHeaderRow.createCell(2).setCellValue("Ricetta Originale");
            tableHeaderRow.createCell(3).setCellValue("Tempo Preparazione (min)");
            
            // Applica stile all'intestazione
            for (int i = 0; i < 4; i++) {
                tableHeaderRow.getCell(i).setCellStyle(headerStyle);
            }
            
            // Contenuto delle sezioni
            for (SezioneMenu sezione : menu.getSezioni()) {
                if (sezione.getRicette().isEmpty()) {
                    // Sezione vuota
                    Row emptyRow = sheet.createRow(rowNum++);
                    Cell sectionCell = emptyRow.createCell(0);
                    sectionCell.setCellValue(sezione.getNome());
                    sectionCell.setCellStyle(sectionStyle);
                    emptyRow.createCell(1).setCellValue("(Sezione vuota)");
                } else {
                    // Ricette nella sezione
                    for (RicettaInMenu ricettaInMenu : sezione.getRicette()) {
                        Row ricettaRow = sheet.createRow(rowNum++);
                        
                        Cell sectionCell = ricettaRow.createCell(0);
                        sectionCell.setCellValue(sezione.getNome());
                        sectionCell.setCellStyle(sectionStyle);
                        
                        ricettaRow.createCell(1).setCellValue(ricettaInMenu.getNomeNelMenu());
                        ricettaRow.createCell(2).setCellValue(ricettaInMenu.getRicettaOriginale().getNome());
                        ricettaRow.createCell(3).setCellValue(ricettaInMenu.getRicettaOriginale().getTempoPreparazione());
                    }
                }
            }
            
            // Riga con timestamp di esportazione
            rowNum += 2; // Due righe vuote
            Row timestampRow = sheet.createRow(rowNum);
            timestampRow.createCell(0).setCellValue("Esportato il: " + 
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")));
            
            // Adatta larghezza colonne
            for (int i = 0; i < 4; i++) {
                sheet.autoSizeColumn(i);
            }
            
            // Salva il file
            try (FileOutputStream fileOut = new FileOutputStream(filePath)) {
                workbook.write(fileOut);
            }
        }
    }

    
    /**
     * Crea lo stile per le intestazioni.
     */
    private static CellStyle createHeaderStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        font.setFontHeightInPoints((short) 12);
        style.setFont(font);
        style.setFillForegroundColor(IndexedColors.LIGHT_BLUE.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        return style;
    }
    
    /**
     * Crea lo stile per i titoli.
     */
    private static CellStyle createTitleStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        font.setFontHeightInPoints((short) 16);
        style.setFont(font);
        return style;
    }
    
    /**
     * Crea lo stile per le sezioni.
     */
    private static CellStyle createSectionStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        font.setFontHeightInPoints((short) 10);
        style.setFont(font);
        style.setFillForegroundColor(IndexedColors.LIGHT_YELLOW.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        return style;
    }
    
    /**
     * Sanifica un nome per renderlo valido come nome di foglio Excel.
     * Rimuove caratteri non validi e limita la lunghezza.
     */
    private static String sanificaNomeFoglio(String nome) {
        if (nome == null) return "Menu";
        
        // Caratteri non validi per i nomi dei fogli Excel: [ ] * ? : / \
        String nomeSanificato = nome.replaceAll("[\\[\\]\\*\\?\\:\\/\\\\]", "_");
        
        // Limita lunghezza (max 31 caratteri per Excel)
        if (nomeSanificato.length() > 25) {
            nomeSanificato = nomeSanificato.substring(0, 22) + "...";
        }
        
        return nomeSanificato;
    }
} 