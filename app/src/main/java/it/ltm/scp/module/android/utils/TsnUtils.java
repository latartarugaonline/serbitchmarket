package it.ltm.scp.module.android.utils;

import it.ltm.scp.module.android.exceptions.MalformedTsnException;
import it.ltm.scp.module.android.model.devices.pos.tsn.TsnDTO;

/**
 * Created by HW64 on 24/02/2017.
 */

public class TsnUtils {

    public static TsnDTO parseTsnData(String input) throws MalformedTsnException {
        try {

            String SEPARATOR_FIELDS = "=";
            String START_SENTINEL = ";";
            String END_SENTINEL = "?";
            String SEPARATOR_NAME = "  ";

            int INDEX_CF_FIELD = 0;
            int INDEX_NAME_FIELD = 1;

            int INDEX_NAME = 1;
            int INDEX_SURNAME = 0;


            TsnDTO result = new TsnDTO();

            String[] fields = input.split(SEPARATOR_FIELDS);
            String cf = getParsedStream(START_SENTINEL, END_SENTINEL, fields[INDEX_CF_FIELD]);
            if(!isCfValid(cf)){
                throw new MalformedTsnException("Codice fiscale non valido: " + cf);
            }
            result.setCf(cf);
            String nameField = getParsedStream(START_SENTINEL, END_SENTINEL, fields[INDEX_NAME_FIELD]);
            //in caso di lettura da banda magnetica i nomi che finiscono con accetto presentano uno spazio in più, aumentando il separatore a 3 spazi
            nameField = nameField.replace("   ", "'" + SEPARATOR_NAME);
            String[] nameFields = nameField.split(SEPARATOR_NAME);
            String name = nameFields[INDEX_NAME];
            String surname = nameFields[INDEX_SURNAME];

            if(!isNameValid(name)){
                throw new MalformedTsnException("Nome non valido: " + name);
            }

            if(!isNameValid(surname)){
                throw new MalformedTsnException("Cognome non valido: " + surname);
            }

            result.setNome(name);
            result.setCognome(surname);

            return result;

        } catch (Exception e){
            throw new MalformedTsnException(e.getMessage());
        }
    }

    private static boolean isCfValid(String cfField) {
        return cfField.length() == 16 && AppUtils.checkAlphanumericString(cfField);
    }

    private static boolean isNameValid(String input){
        return AppUtils.checkCharOnlyString(input);
    }

    private static String getParsedStream(String start_sentinel, String end_sentinel, String field) {
        int startIndex = field.indexOf(start_sentinel);
        int endIndex = field.indexOf(end_sentinel);
        return field.substring(startIndex +1, endIndex);
    }

}
