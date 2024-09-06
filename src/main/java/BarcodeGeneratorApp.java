import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.ByteArrayOutputStream;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.pdf417.PDF417Writer;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.util.regex.*;

public class BarcodeGeneratorApp extends JFrame implements ActionListener {
    private JTextField inputCustomerID, inputLastName, inputFirstName, inputMiddleName, inputDOB, inputExpDate, inputHeight, inputEyeColor, inputStreet, inputCity, inputState, inputZip, inputIssueDate, inputSuffix, inputWeight, inputHairColor;
    private JLabel barcodeLabel;

    public BarcodeGeneratorApp() {
        // Frame setup
        setTitle("AAMVA PDF417 Barcode Generator");
        setSize(700, 500);
        setLayout(new GridLayout(20, 2));

        // Input fields based on the updated 2020 AAMVA Standard
        add(new JLabel("Customer ID (DAQ - 9 characters):"));
        inputCustomerID = new JTextField();
        add(inputCustomerID);

        add(new JLabel("Last Name (DCS - 40 characters):"));
        inputLastName = new JTextField();
        add(inputLastName);

        add(new JLabel("First Name (DAC - 40 characters):"));
        inputFirstName = new JTextField();
        add(inputFirstName);

        add(new JLabel("Middle Name (DAD - 40 characters):"));
        inputMiddleName = new JTextField();
        add(inputMiddleName);

        add(new JLabel("Date of Birth (DBB - MMDDCCYY):"));
        inputDOB = new JTextField();
        add(inputDOB);

        add(new JLabel("Expiration Date (DBA - MMDDCCYY):"));
        inputExpDate = new JTextField();
        add(inputExpDate);

        add(new JLabel("Issue Date (DBD - MMDDCCYY):"));
        inputIssueDate = new JTextField();
        add(inputIssueDate);

        add(new JLabel("Height (DAU - inches, max 6 characters):"));
        inputHeight = new JTextField();
        add(inputHeight);

        add(new JLabel("Eye Color (DAY - 3 characters):"));
        inputEyeColor = new JTextField();
        add(inputEyeColor);

        add(new JLabel("Street Address (DAG - 35 characters):"));
        inputStreet = new JTextField();
        add(inputStreet);

        add(new JLabel("City (DAI - 20 characters):"));
        inputCity = new JTextField();
        add(inputCity);

        add(new JLabel("State (DAJ - 2 characters):"));
        inputState = new JTextField();
        add(inputState);

        add(new JLabel("Zip Code (DAK - 11 characters):"));
        inputZip = new JTextField();
        add(inputZip);

        add(new JLabel("Suffix (DCU - max 5 characters):"));
        inputSuffix = new JTextField();
        add(inputSuffix);

        add(new JLabel("Weight (DAW - pounds, max 3 characters):"));
        inputWeight = new JTextField();
        add(inputWeight);

        add(new JLabel("Hair Color (DAZ - 3 characters):"));
        inputHairColor = new JTextField();
        add(inputHairColor);

        JButton generateButton = new JButton("Generate Barcode");
        add(generateButton);
        generateButton.addActionListener(this);

        barcodeLabel = new JLabel();
        add(barcodeLabel);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }

    public static void main(String[] args) {
        new BarcodeGeneratorApp();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String customerID = validateText(inputCustomerID.getText(), 9, "DAQ");
        String lastName = validateText(inputLastName.getText(), 40, "DCS");
        String firstName = validateText(inputFirstName.getText(), 40, "DAC");
        String middleName = validateText(inputMiddleName.getText(), 40, "DAD");
        String dob = validateDate(inputDOB.getText(), "DBB");
        String expDate = validateDate(inputExpDate.getText(), "DBA");
        String issueDate = validateDate(inputIssueDate.getText(), "DBD");
        String height = validateText(inputHeight.getText(), 6, "DAU");
        String eyeColor = validateText(inputEyeColor.getText(), 3, "DAY");
        String street = validateText(inputStreet.getText(), 35, "DAG");
        String city = validateText(inputCity.getText(), 20, "DAI");
        String state = validateText(inputState.getText(), 2, "DAJ");
        String zip = validateText(inputZip.getText(), 11, "DAK");
        String suffix = validateText(inputSuffix.getText(), 5, "DCU");
        String weight = validateText(inputWeight.getText(), 3, "DAW");
        String hairColor = validateText(inputHairColor.getText(), 3, "DAZ");

        // Data format according to AAMVA standard in PDF
        String barcodeData = "@ANSI 636054090002DL00410301ZN03420053DL"
                            + "DAQ" + customerID
                            + "DCS" + lastName
                            + "DAC" + firstName
                            + "DAD" + middleName
                            + "DBB" + dob
                            + "DBA" + expDate
                            + "DBD" + issueDate
                            + "DAU" + height
                            + "DAY" + eyeColor
                            + "DAG" + street
                            + "DAI" + city
                            + "DAJ" + state
                            + "DAK" + zip
                            + "DCU" + suffix
                            + "DAW" + weight
                            + "DAZ" + hairColor;

        try {
            generateBarcode(barcodeData);
        } catch (WriterException | IOException ex) {
            ex.printStackTrace();
        }
    }

    private void generateBarcode(String data) throws WriterException, IOException {
        PDF417Writer writer = new PDF417Writer();
        // Higher resolution (600 dpi)
        BitMatrix bitMatrix = writer.encode(data, BarcodeFormat.PDF_417, 600, 300);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(MatrixToImageWriter.toBufferedImage(bitMatrix), "png", baos);
        ImageIcon barcodeIcon = new ImageIcon(baos.toByteArray());

        barcodeLabel.setIcon(barcodeIcon);

        // Save barcode as a file
        File outputfile = new File("barcode.png");
        ImageIO.write(MatrixToImageWriter.toBufferedImage(bitMatrix), "png", outputfile);
    }

    // Validate input text fields based on length
    private String validateText(String text, int maxLength, String fieldName) {
        if (text.length() > maxLength) {
            throw new IllegalArgumentException(fieldName + " exceeds maximum length of " + maxLength);
        }
        return text;
    }

    // Validate dates in MMDDCCYY format
    private String validateDate(String date, String fieldName) {
        if (!Pattern.matches("\\d{2}\\d{2}\\d{4}", date)) {
            throw new IllegalArgumentException(fieldName + " must be in MMDDCCYY format");
        }
        return date;
    }
}
