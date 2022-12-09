package br.com.oi.sgis.util;

import br.com.oi.sgis.dto.LabelParametersDTO;
import net.sourceforge.barbecue.Barcode;
import net.sourceforge.barbecue.BarcodeException;
import net.sourceforge.barbecue.BarcodeFactory;
import net.sourceforge.barbecue.BarcodeImageHandler;
import net.sourceforge.barbecue.output.OutputException;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Objects;

public class LabelUtils {

    private LabelUtils(){}

    public static BufferedImage getBarcodeImage(String id) throws BarcodeException, OutputException{
        try {
            if(hasCharString(id)){
                return creteDuploBarcode(id);
            }else{
                return getBarcode(id);
            }
        }catch (RuntimeException e){
            return null;
        }
    }

    public static BufferedImage getBarcode128A(String id) throws BarcodeException, OutputException {
        Barcode barcodeModel = BarcodeFactory.createCode128A(id);
        barcodeModel.setFont(Font.getFont(Font.MONOSPACED));
        barcodeModel.setBarHeight(700);
        barcodeModel.setBarWidth(30);
        barcodeModel.setLabel(id);
        return BarcodeImageHandler.getImage(barcodeModel);
    }

    private static boolean hasCharString(String id){
        long count = 0;
        if(id.length()>17)
            count =  id.substring(17,20).chars().filter(Character::isLetter).count();
        return count > 0;
    }

    private static BufferedImage creteDuploBarcode(String id) throws BarcodeException, OutputException {

        String ean128C = id.substring(0,16);
        String ean128A = id.substring(16);
        Barcode barcodeModel128C = BarcodeFactory.createCode128C(ean128C);
        barcodeModel128C.setFont(Font.getFont(Font.MONOSPACED));
        barcodeModel128C.setBarHeight(400);
        barcodeModel128C.setBarWidth(30);
        barcodeModel128C.setLabel(id);

        Barcode barcodeModel128A = BarcodeFactory.createCode128A(ean128A);
        barcodeModel128A.setFont(Font.getFont(Font.MONOSPACED));
        barcodeModel128A.setBarHeight(400);
        barcodeModel128A.setBarWidth(30);
        barcodeModel128A.setLabel(id);

        BufferedImage image1 = BarcodeImageHandler.getImage(barcodeModel128C);
        BufferedImage image2 = BarcodeImageHandler.getImage(barcodeModel128A);

        return joinBufferedImage(image1, image2);
    }

    private static BufferedImage joinBufferedImage(BufferedImage img1,
                                                   BufferedImage img2) {
        int width = img1.getWidth();
        int height = img1.getHeight() + img2.getHeight() + 50;
        BufferedImage newImage = new BufferedImage(width, height,
                BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = newImage.createGraphics();
        Color oldColor = g2.getColor();
        g2.setPaint(Color.WHITE);
        g2.fillRect(0, 0, width, height );
        g2.setColor(oldColor);
        g2.drawImage(img1, null, 0, 0);
        g2.drawImage(img2, null, 0, img1.getHeight() + 50);
        g2.dispose();
        return newImage;
    }

    private static BufferedImage getBarcode(String id) throws BarcodeException, OutputException {
        Barcode barcodeModel = BarcodeFactory.createCode128C(id);
        barcodeModel.setFont(Font.getFont(Font.MONOSPACED));
        barcodeModel.setBarHeight(700);
        barcodeModel.setBarWidth(30);
        barcodeModel.setLabel(id);
        return BarcodeImageHandler.getImage(barcodeModel);
    }

    public static BufferedImage getBarcodePersonalized(LabelParametersDTO parametersDTO, String id, Boolean isTest) throws BarcodeException, OutputException, IOException {
        Barcode barcodeModel = BarcodeFactory.createCode128C(id);
        barcodeModel.setFont(Font.getFont(Font.MONOSPACED));
        barcodeModel.setBarHeight(parametersDTO.getVerticalRange().intValue());
        barcodeModel.setBarWidth(parametersDTO.getHorizontalRange().intValue());
        barcodeModel.setLabel(id);
        barcodeModel.setDrawingText(true);

        BufferedImage image = BarcodeImageHandler.getImage(barcodeModel);
        BufferedImage label = new BufferedImage(toPixel(parametersDTO.getLabelWidth()), toPixel(parametersDTO.getLabelHeight()), image.getType());
        Graphics2D g = label.createGraphics();

        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g.setPaint(Color.WHITE);
        g.fillRect(0, 0, toPixel(parametersDTO.getLabelWidth()), toPixel(parametersDTO.getLabelHeight()));
        g.drawImage(image, toPixel(parametersDTO.getLateralMargin()), toPixel(parametersDTO.getTopMargin()), toPixel(parametersDTO.getLabelWidth()), toPixel(parametersDTO.getLabelHeight()), 0, 0, image.getWidth(), image.getHeight(), null);

        if(Boolean.TRUE.equals(isTest)) {
            BufferedImage test = ImageIO.read(Objects.requireNonNull(LabelUtils.class.getResource("/reports/images/labelDeTeste.png")));
            g.drawImage(test, toPixel(parametersDTO.getLateralMargin()), toPixel(parametersDTO.getTopMargin()), toPixel(parametersDTO.getLabelWidth()), toPixel(parametersDTO.getLabelHeight()), 0, 0, test.getWidth(), test.getHeight(), null);
        }
        g.dispose();
        return label;
    }

    public static BufferedImage getNoBarcodeOnTest(LabelParametersDTO parametersDTO) throws IOException {
        if(Boolean.TRUE.equals(parametersDTO.getOnlyTest())) {
            BufferedImage test = ImageIO.read(Objects.requireNonNull(LabelUtils.class.getResource("/reports/images/labelDeTeste.png")));
            BufferedImage whiteLabel = new BufferedImage(toPixel(parametersDTO.getLabelWidth()), toPixel(parametersDTO.getLabelHeight()), test.getType());

            Graphics2D g = whiteLabel.createGraphics();
            g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                    RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            g.setPaint(Color.WHITE);
            g.fillRect(0, 0, toPixel(parametersDTO.getLabelWidth()), toPixel(parametersDTO.getLabelHeight()));
            g.drawImage(test, toPixel(parametersDTO.getLateralMargin()), toPixel(parametersDTO.getTopMargin()), toPixel(parametersDTO.getLabelWidth()), toPixel(parametersDTO.getLabelHeight()), 0, 0, test.getWidth(), test.getHeight(), null);

            return whiteLabel;
        }
        return null;
    }


    private static int toPixel(Long value){
        return (int) (value.intValue() * 3.779528);
    }


}
