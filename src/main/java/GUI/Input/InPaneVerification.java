package main.java.GUI.Input;

import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import main.java.BasicNetworking.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class InPaneVerification
{
    public boolean isFlawless = true;

    private enum ExpectedIPType
    {
        BOTH, IPV4, IPV6
    }

    public String verifyObligatoryIP(TextField textField)
    {
        verifyObligatoryIP(textField, ExpectedIPType.BOTH);
        return textField.getText();
    }

    public String verifyObligatoryIPv4(TextField textField)
    {
        verifyObligatoryIP(textField, ExpectedIPType.IPV4);
        return textField.getText();
    }

    public String verifyObligatoryIPv6(TextField textField)
    {
        verifyObligatoryIP(textField, ExpectedIPType.IPV6);
        return textField.getText();
    }

    public String verifyNonObligatoryIP(TextField textField)
    {
        verifyNonObligatoryIP(textField, ExpectedIPType.BOTH);
        return textField.getText();
    }

    public String verifyNonObligatoryIPv4(TextField textField)
    {
        verifyNonObligatoryIP(textField, ExpectedIPType.IPV4);
        return textField.getText();
    }

    public String verifyNonObligatoryIPv6(TextField textField)
    {
        verifyNonObligatoryIP(textField, ExpectedIPType.IPV6);
        return textField.getText();
    }

    public String verifyObligatoryInput(TextField textField)
    {
        verifyObligatoryInputLogic(textField);
        return textField.getText();
    }

    public int verifyObligatoryNumberInput(TextField textField)
    {
        return verifyObligatoryNumberInputLogic(textField);
    }

    public int verifyNonObligatoryNumberInput(TextField textField)
    {
        return verifyNonObligatoryNumberInputLogic(textField);
    }

    public String verifyIPv4Mask(TextField textField)
    {
        verifyIPv4MaskLogic(textField);
        return textField.getText();
    }

    public String verifyIPv6Prefix(TextField textField)
    {
        verifyIPv6prefixLogic(textField);
        return textField.getText();
    }

    public String verifyMaskOrPrefix(TextField textField)
    {
        verifyMaskLogic(textField);
        return textField.getText();
    }

    public IPNetwork verifyObligatoryNetwork(TextField ip, TextField mask)
    {
        return verifyObligatoryNetworkLogic(ip, mask);
    }

    private void verifyNonObligatoryIP(TextField textField, ExpectedIPType type)
    {
        try
        {
            isEmptyOrNullInput(textField);
            if (type == ExpectedIPType.BOTH)
            {
                IPAddressValidator.isValid(textField.getText());
            }
            else
            {
                if (type == ExpectedIPType.IPV4)
                {
                    IPAddressValidator.isValidIPv4(textField.getText());
                }
                else
                {
                    IPAddressValidator.isValidIPv6(textField.getText());
                }
            }
            makeTextFieldNormal(textField);
        }
        catch (InvalidIPException e)
        {
            isFlawless = false;
            makeTextFieldRed(textField);
            // Non-obligatory was used but wrongly
        }
        catch (EmptyOrNullInputException ignored)
        {
            // Non-obligatory not used
        }
    }

    private void verifyObligatoryIP(TextField textField, ExpectedIPType type)
    {
        try
        {
            isEmptyOrNullInput(textField);
            if (type == ExpectedIPType.BOTH)
            {
                IPAddressValidator.isValid(textField.getText());
            }
            else
            {
                if (type == ExpectedIPType.IPV4)
                {
                    IPAddressValidator.isValidIPv4(textField.getText());
                }
                else
                {
                    IPAddressValidator.isValidIPv6(textField.getText());
                }
            }
            makeTextFieldNormal(textField);
        }
        catch (EmptyOrNullInputException | InvalidIPException e)
        {
            isFlawless = false;
            makeTextFieldRed(textField);
        }
    }

    private @Nullable IPNetwork verifyObligatoryNetworkLogic(TextField ipTextField, TextField maskTextField)
    {
        try
        {
            isEmptyOrNullInput(ipTextField);
            isEmptyOrNullInput(maskTextField);
            try
            {
                IPAddressValidator.isValidIPv4(ipTextField.getText());
                if (maskTextField.getText().split("\\.").length == 4)
                {
                    IPAddressValidator.isValidIpv4Network("1.1.1.1", maskTextField.getText());
                }
                else
                {
                    String mask = maskTextField.getText();
                    if (mask.contains("/"))
                    {
                        mask = mask.substring(1);
                    }
                    mask = IPAddressValidator.convertIpv4PrefixToMask(mask);
                    IPAddressValidator.isValidIpv4Network("1.1.1.1", mask);
                }
                makeTextFieldNormal(ipTextField);
                makeTextFieldNormal(maskTextField);
                return new IPv4Network(ipTextField.getText(), maskTextField.getText());
            }
            catch (InvalidIPException | IllegalArgumentException e)
            {
                IPAddressValidator.isValidIPv6(ipTextField.getText());
                String mask = maskTextField.getText();
                if (mask.contains("/"))
                {
                    mask = mask.substring(1);
                }
                IPAddressValidator.isValidIpv6Network("2000::1", Integer.parseInt(mask));
                makeTextFieldNormal(ipTextField);
                makeTextFieldNormal(maskTextField);
                return new IPv6Network(ipTextField.getText(), maskTextField.getText());
            }
        }
        catch (IllegalArgumentException | EmptyOrNullInputException | InvalidIPException e)
        {
            isFlawless = false;
            makeTextFieldRed(ipTextField);
            makeTextFieldRed(maskTextField);
            return null;
        }
    }

    private void verifyObligatoryInputLogic(TextField textField)
    {
        try
        {
            isEmptyOrNullInput(textField);
            makeTextFieldNormal(textField);
        }
        catch (EmptyOrNullInputException e)
        {
            isFlawless = false;
            makeTextFieldRed(textField);
        }
    }

    private int verifyObligatoryNumberInputLogic(TextField textField)
    {
        int parsed = -1;
        try
        {
            isEmptyOrNullInput(textField);                      // TextField can't be empty
            parsed = Integer.parseInt(textField.getText());     // TextField must be parse-able
            if (parsed < 0)                                     // Number must be positive or zero
            {
                isFlawless = false;
                makeTextFieldRed(textField);
            }
            else
            {
                makeTextFieldNormal(textField);
            }
        }
        catch (EmptyOrNullInputException | NumberFormatException e)
        {
            isFlawless = false;
            makeTextFieldRed(textField);
        }
        return parsed;
    }

    private int verifyNonObligatoryNumberInputLogic(TextField textField)
    {
        int parsed = -1;
        try
        {
            isEmptyOrNullInput(textField);                       // If the TextField is empty, then it can be ignored
            parsed = Integer.parseInt(textField.getText());     // TextField must be parse-able
            if (parsed < 0)                                     // Number must be positive or zero
            {
                isFlawless = false;
                makeTextFieldRed(textField);
            }
            else
            {
                makeTextFieldNormal(textField);
            }
        }
        catch (EmptyOrNullInputException e)
        {
            makeTextFieldNormal(textField);
        }
        catch (NumberFormatException e)
        {
            isFlawless = false;
            makeTextFieldRed(textField);
        }
        return parsed;
    }

    private void verifyIPv4MaskLogic(TextField textField)
    {
        try
        {
            isEmptyOrNullInput(textField);
            if (textField.getText().split("\\.").length == 4)
            {
                IPAddressValidator.isValidIpv4Network("1.1.1.1", textField.getText());
            }
            else
            {
                String mask = textField.getText();
                if (mask.contains("/"))
                {
                    mask = mask.substring(1);
                }
                mask = IPAddressValidator.convertIpv4PrefixToMask(mask);
                IPAddressValidator.isValidIpv4Network("1.1.1.1", mask);
            }
            makeTextFieldNormal(textField);
        }
        catch (EmptyOrNullInputException | IllegalArgumentException e)
        {
            isFlawless = false;
            makeTextFieldRed(textField);
        }
    }

    private void verifyIPv6prefixLogic(TextField textField)
    {
        try
        {
            isEmptyOrNullInput(textField);
            IPAddressValidator.isValidIpv6Network("2000::1", Integer.parseInt(textField.getText()));
            makeTextFieldNormal(textField);
        }
        catch (EmptyOrNullInputException | IllegalArgumentException e)
        {
            isFlawless = false;
            makeTextFieldRed(textField);
        }
    }

    private void verifyMaskLogic(TextField textField)
    {
        try
        {
            isEmptyOrNullInput(textField);
            try
            {
                if (textField.getText().split("\\.").length == 4)
                {
                    IPAddressValidator.isValidIpv4Network("1.1.1.1", textField.getText());
                }
                else
                {
                    String mask = textField.getText();
                    if (mask.contains("/"))
                    {
                        mask = mask.substring(1);
                    }
                    mask = IPAddressValidator.convertIpv4PrefixToMask(mask);
                    IPAddressValidator.isValidIpv4Network("1.1.1.1", mask);
                }
                makeTextFieldNormal(textField);
            }
            catch (IllegalArgumentException e)
            {
                try
                {

                    String mask = textField.getText();
                    if (mask.contains("/"))
                    {
                        mask = mask.substring(1);
                    }
                    IPAddressValidator.isValidIpv6Network("2000::1", Integer.parseInt(mask));
                    makeTextFieldNormal(textField);
                }
                catch (IllegalArgumentException ex)
                {
                    isFlawless = false;
                    makeTextFieldRed(textField);
                }
            }
        }
        catch (EmptyOrNullInputException e)
        {
            isFlawless = false;
            makeTextFieldRed(textField);
        }
    }


    public void isEmptyOrNullInput(@NotNull TextField textField)
    {
        if (textField.getText() == null || textField.getText().isEmpty())
        {
            throw new EmptyOrNullInputException("The Text field " + textField.getId() + " had an unclean input.");
        }
    }

    public void makeTextFieldRed(@NotNull TextField textField)
    {
        Border redBorder = new Border(new BorderStroke(Color.RED, BorderStrokeStyle.SOLID, new CornerRadii(4), new BorderWidths(1)));
        textField.setBorder(redBorder);
    }

    public void makeTextFieldNormal(@NotNull TextField textField)
    {
        TextField textField1 = new TextField();
        textField.setBorder(textField1.getBorder());
    }
}
