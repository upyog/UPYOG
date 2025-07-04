
package org.egov.finance.report.util;

import java.math.BigDecimal;
import java.math.RoundingMode;

import static org.egov.finance.report.util.LocalizationSettings.currencyName;
import static org.egov.finance.report.util.LocalizationSettings.currencyNamePlural;
import static org.egov.finance.report.util.LocalizationSettings.currencyUnitName;
import static org.egov.finance.report.util.LocalizationSettings.currencyUnitNamePlural;
import static org.egov.finance.report.util.ReportConstants.WHITESPACE;

public final class NumberToWordConverter {

    private static final String ZERO = " Zero ";
    private static final String HUNDRED = " Hundred ";
    private static final String THOUSAND = " Thousand ";
    private static final String LAKH = " Lakh ";
    private static final String CRORE = " Crore ";

    private static final String[] WORDS_FOR_MULTIPLES_OF_TENS = {"Ten", "Twenty", "Thirty", "Forty", "Fifty", "Sixty",
            "Seventy", "Eighty", "Ninety"};
    private static final String[] WORDS_FOR_TENS = {"Eleven", "Twelve", "Thirteen", "Fourteen", "Fifteen", "Sixteen",
            "Seventeen", "Eighteen", "Nineteen"};
    private static final String[] WORDS_FOR_NUMBER = {"One", "Two", "Three", "Four", "Five", "Six", "Seven",
            "Eight", "Nine"};

    private NumberToWordConverter() {
        //Only static API's
    }

    public static String amountInWordsWithCircumfix(BigDecimal amount) {
        return numberToWords(amount, true, true);
    }

    public static String numberToWords(BigDecimal number, boolean prefix, boolean suffix) {
        StringBuilder numberInWords = new StringBuilder();
        if (prefix) {
            if (number.intValue() < 2) {
                numberInWords.append(currencyName()).append(WHITESPACE).append(convertToWords(number));
            } else {
                numberInWords.append(currencyNamePlural()).append(WHITESPACE).append(convertToWords(number));
            }
        } else {
            numberInWords.append(convertToWords(number));
        }

        if (suffix) {
            numberInWords.append(" Only");
        }
        return numberInWords.toString();
    }

    private static String convertToWords(BigDecimal value) {
        BigDecimal givenNumber = value;
        boolean negativeNumber = givenNumber.signum() == -1;
        if (negativeNumber) {
            givenNumber = givenNumber.abs();
        }

        String numberString = givenNumber.setScale(2, RoundingMode.HALF_UP).toPlainString();
        StringBuilder numberInWord = convertIntegerPartToWord(Double.parseDouble(numberString));

        if (numberInWord.toString().trim().length() == 0) {
            numberInWord.append(ZERO);
        }

        String result = numberInWord.toString().trim();
        if (negativeNumber) {
            result = "Minus " + result;
        }

        return result;

    }

    private static StringBuilder convertIntegerPartToWord(double number) {
        StringBuilder word = new StringBuilder();
        int quotient = (int) (number / 10000000);
        if (quotient > 0) {
            word.append(convertToWords(new BigDecimal(quotient))).append(CRORE);
        }

        number = number % 10000000;
        quotient = (int) (number / 100000);
        if (quotient > 0) {
            word.append(numberToWord(quotient)).append(LAKH);
        }

        number = number % 100000;
        quotient = (int) (number / 1000);
        if (quotient > 0) {
            word.append(numberToWord(quotient)).append(THOUSAND);
        }

        number = number % 1000;
        quotient = (int) (number / 100);
        if (quotient > 0) {
            word.append(numberToWord(quotient)).append(HUNDRED);
        }

        number = number % 100;
        if (number != 0) {
            word.append(numberToWord((int) number)).append(WHITESPACE);
        }

        convertFractionalPartToWord(word, number);

        return word;
    }

    private static void convertFractionalPartToWord(StringBuilder word, double number) {
        int fractionalPart;
        if (number % 1 != 0) {
            String fraction = Double.toString(number).split("\\.")[1];
            if (fraction.length() > 2) {
                fractionalPart = Integer.parseInt(fraction.substring(0, 2));
                if (Integer.parseInt(fraction.substring(2, 3)) > 5) {
                    fractionalPart++;
                }
            } else {
                fractionalPart = Integer.parseInt(fraction);
            }
            if (fraction.length() == 1) {
                fractionalPart *= 10;
            }
            if (word.toString().trim().length() > 0) {
                word.append(("and "));
            }

            word.append(numberToWord(fractionalPart));

            if (fractionalPart <= 1) {
                word.append(WHITESPACE).append(currencyUnitName());
            } else {
                word.append(WHITESPACE).append(currencyUnitNamePlural());
            }
        }
    }

    private static String numberToWord(int number) {
        int quotient = (number / 10);
        StringBuilder word = new StringBuilder();
        if (quotient > 0) {
            if (quotient == 1 && (number % 10) > 0) {
                word.append(WORDS_FOR_TENS[(number % 10) - 1]);
                return word.toString();
            }
            word.append(WORDS_FOR_MULTIPLES_OF_TENS[quotient - 1]);
        }
        int remainder = number % 10;
        if (remainder > 0) {
            if (word.length() > 0) {
                word.append(WHITESPACE);
            }
            word.append(WORDS_FOR_NUMBER[remainder - 1]);
        }
        return word.toString();
    }
}
