package it.ltm.scp.module.android.managers.cie;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MRZ
{
    private static final int MRZ_STRING_LENGTH = 90;
    private static final Map<String, Integer> alphabetMapping = new HashMap<String, Integer>()
    {
        {
            put("<", 0);
            put("0", 0);
            put("1", 1);
            put("2", 2);
            put("3", 3);
            put("4", 4);
            put("5", 5);
            put("6", 6);
            put("7", 7);
            put("8", 8);
            put("9", 9);
            put("A", 10);
            put("B", 11);
            put("C", 12);
            put("D", 13);
            put("E", 14);
            put("F", 15);
            put("G", 16);
            put("H", 17);
            put("I", 18);
            put("J", 19);
            put("K", 20);
            put("L", 21);
            put("M", 22);
            put("N", 23);
            put("O", 24);
            put("P", 25);
            put("Q", 26);
            put("R", 27);
            put("S", 28);
            put("T", 29);
            put("U", 30);
            put("V", 31);
            put("W", 32);
            put("X", 33);
            put("Y", 34);
            put("Z", 35);
        }
    };

    private static final List<Integer> weights = new ArrayList<Integer>()
    {
        {
            add(0, 7);
            add(1, 3);
            add(2, 1);
        }
    };

    private String doc;
    private String country;
    private String documentNumber;
    private String documentNumberHash;
    private String optionalData1;
    private String birthDate;
    private String birthDateHash;
    private String gender;
    private String expiryDate;
    private String expiryDateHash;
    private String nationality;
    private String optionalData2;
    private String overallHash;
    private String primaryIdentifier = "";
    private String secondaryIdentifier = "";

    public MRZ(String iFlatMRZString)
    {
        parse(iFlatMRZString);
    }

    private void parse(String iFlatMRZString)
    {
        if(iFlatMRZString.length() == MRZ_STRING_LENGTH)
        {
            doc                     = iFlatMRZString.substring(0, 2);
            country                 = iFlatMRZString.substring(2, 5);
            documentNumber          = iFlatMRZString.substring(5, 14);
            documentNumberHash      = iFlatMRZString.substring(14, 15);
            optionalData1           = iFlatMRZString.substring(15, 30);
            birthDate               = iFlatMRZString.substring(30, 36);
            birthDateHash           = iFlatMRZString.substring(36, 37);
            gender                  = iFlatMRZString.substring(37, 38);
            expiryDate              = iFlatMRZString.substring(38, 44);
            expiryDateHash          = iFlatMRZString.substring(44, 45);
            nationality             = iFlatMRZString.substring(45, 48);
            optionalData2           = iFlatMRZString.substring(48, 59);
            overallHash             = iFlatMRZString.substring(59, 60);

            String thirdRow         = iFlatMRZString.substring(60, 90);
            String[] identifiers    = thirdRow.split("<<");
            System.out.println(Arrays.toString(identifiers));
            if(identifiers.length >= 2)
            {
                String[] primaryIdentifiers = identifiers[1].split("<");
                for(int i = 0; i < primaryIdentifiers.length; ++i) {
                    primaryIdentifier += primaryIdentifiers[i] + (i == primaryIdentifiers.length - 1 ? "" : " " );
                }
                String[] secondaryIdentifiers = identifiers[0].split("<");
                for(int i = 0; i < secondaryIdentifiers.length; ++i) {
                    secondaryIdentifier += secondaryIdentifiers[i] + (i == secondaryIdentifiers.length - 1 ? "" : " " );
                }
            }
        }
    }

    // This check is provided by the standard ICAO: see APPENDIX A TO PART 3 — EXAMPLES OF CHECK DIGIT CALCULATION
    // of https://www.icao.int/publications/Documents/9303_p3_cons_en.pdf
    private boolean checkHash(String iData, int iDataLength, String iDataHash, int iDataHashLength)
    {
        boolean isValid = false;
        if(iData.length() == iDataLength && iDataHash.length() == iDataHashLength)
        {
            int sum = 0;
            for(int i = 0; i < iData.length(); ++i)
            {
                String currentChar = Character.toString(iData.charAt(i));
                int value = alphabetMapping.get(currentChar);
                sum += value * weights.get(i % 3);
            }
            int check = sum % 10;
            isValid = check == Integer.parseInt(iDataHash);
        }
        return isValid;
    }

    public boolean isValid()
    {
        String overallHashString =
                documentNumber +
                documentNumberHash +
                optionalData1 +
                birthDate +
                birthDateHash +
                expiryDate +
                expiryDateHash +
                optionalData2;

        return
                checkHash(documentNumber, 9, documentNumberHash, 1) &&
                checkHash(birthDate, 6, birthDateHash, 1) &&
                checkHash(expiryDate, 6, expiryDateHash, 1) &&
                checkHash(overallHashString, 50, overallHash, 1);
    }

    // public static void main(String[] args)
    // {
    //     MRZ mrz = new MRZ("C<ITACA84974DG7<<<<<<<<<<<<<<<8311151M2911157ITA<<<<<<<<<<<0MICAGLIO<<ALESSANDRO<<<<<<<<<<");
    //     System.out.println("mrz = " + mrz.toString() + ", isValid = " + mrz.isValid());
    // }

    public String getDoc() {
        return doc;
    }

    public String getCountry() {
        return country;
    }

    public String getDocumentNumber() {
        return documentNumber;
    }

    public String getDocumentNumberHash() {
        return documentNumberHash;
    }

    public String getOptionalData1() {
        return optionalData1;
    }

    public String getBirthDate() {
        return birthDate;
    }

    public String getBirthDateHash() {
        return birthDateHash;
    }

    public String getGender() {
        return gender;
    }

    public String getExpiryDate() {
        return expiryDate;
    }

    public String getExpiryDateHash() {
        return expiryDateHash;
    }

    public String getNationality() {
        return nationality;
    }

    public String getOptionalData2() {
        return optionalData2;
    }

    public String getOverallHash() {
        return overallHash;
    }

    public String getPrimaryIdentifier() {
        return primaryIdentifier;
    }

    public String getSecondaryIdentifier() {
        return secondaryIdentifier;
    }

    @Override
    public String toString() {
        return "MRZ{" +
                "doc='" + doc + '\'' +
                ", country='" + country + '\'' +
                ", documentNumber='" + documentNumber + '\'' +
                ", documentNumberHash='" + documentNumberHash + '\'' +
                ", optionalData1='" + optionalData1 + '\'' +
                ", birthDate='" + birthDate + '\'' +
                ", birthDateHash='" + birthDateHash + '\'' +
                ", gender='" + gender + '\'' +
                ", expiryDate='" + expiryDate + '\'' +
                ", expiryDateHash='" + expiryDateHash + '\'' +
                ", nationality='" + nationality + '\'' +
                ", optionalData2='" + optionalData2 + '\'' +
                ", overallHash='" + overallHash + '\'' +
                ", primaryIdentifier='" + primaryIdentifier + '\'' +
                ", secondaryIdentifier='" + secondaryIdentifier + '\'' +
                '}';
    }
}
