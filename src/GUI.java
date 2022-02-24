import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;

public class GUI extends JFrame{
    private JPanel mainPanel;
    private JTable table1;
    private JTextArea textArea1;
    private JComboBox<String> comboBox1;
    private JLabel symbolCountLabel;
    private JSpinner spinnerCol;
    private JSpinner spinnerRow;
    private JCheckBox text2ImageCheckBox;
    private JLabel warningLabel;
    private JLabel pixelCountLabel;
    private JLabel inputPixelLabel;

    private int lastEditedRow = -1;
    private int lastEditedCol = -1;

    private EncodingType currentEncoder = new UncompressedEncoder();

    public GUI() {
        this.setContentPane(mainPanel);
        this.setVisible(true);
        this.setSize(1000,600);
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);

        spinnerRow.setValue(4);
        spinnerCol.setValue(4);

        updateTableModel();
        updateTextField();

        comboBox1.setModel(new DefaultComboBoxModel<>(new String[]{"Uncompressed", "Simple Compression"}));
        comboBox1.addActionListener(e -> {
            int index = comboBox1.getSelectedIndex();
            switch (index) {
                case 0:
                    currentEncoder = new UncompressedEncoder();
                    break;
                case 1:
                    currentEncoder = new SimpleEncoder();
                    break;
                default:
                    System.err.println("Encoder needs to be implemented");
                    currentEncoder = new UncompressedEncoder();
            }
            inputTextChanged();
            updateTextField();
        });

        spinnerRow.addChangeListener(e -> updateTableModel());
        spinnerCol.addChangeListener(e -> updateTableModel());

        textArea1.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                inputTextChanged();
            }
            @Override
            public void removeUpdate(DocumentEvent e) {
                inputTextChanged();
            }
            @Override
            public void changedUpdate(DocumentEvent e) {
                inputTextChanged();
            }
        });
        text2ImageCheckBox.addActionListener(e -> {
            inputTextChanged();
            updateTextField();
        });
    }
    private void inputTextChanged() {
        symbolCountLabel.setText(String.valueOf( textArea1.getText().length() ));
        if(text2ImageCheckBox.isSelected()) {
            String str = currentEncoder.textChanged(textArea1.getText());
            setTablePattern(str);
        }
    }

    private void setTablePattern(String input) {
        inputPixelLabel.setText(String.valueOf(input.length()));
        int colLength = table1.getColumnCount();
        int rowLength = table1.getRowCount();

        int i = 0;
        for (; i < input.length(); i++) {
            int col = i % colLength;
            int row = i / colLength;
            if(row >= rowLength) continue;

            if(input.charAt(i) == '0')
                table1.setValueAt(null, row, col);
            else
                table1.setValueAt("",row,col);
        }

        if(i < rowLength*colLength) {
            warningLabel.setForeground(new Color(241, 87, 87));
            warningLabel.setText("Input Pixels too short");
        }
        else if(i > rowLength*colLength) {
            warningLabel.setForeground(new Color(241, 87, 87));
            warningLabel.setText("Input Pixels too long");
        }
        else {
            warningLabel.setForeground(Color.black);
            warningLabel.setText("No warnings");
        }

        for(; i < rowLength*colLength;i++) {
            int col = i % colLength;
            int row = i / colLength;

            table1.setValueAt(null, row, col);
        }
    }

    private void updateTableModel() {
        table1.setModel( new DefaultTableModel( (Integer)spinnerRow.getValue(), (Integer)spinnerCol.getValue() ) );
        table1.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table1.setDefaultEditor(Object.class, null);


        table1.getSelectionModel().addListSelectionListener(this::listSelectionModel);
        table1.getColumnModel().getSelectionModel().addListSelectionListener(this::listSelectionModel);

        for (int i = 0; i < table1.getColumnCount(); i++) {
            table1.getColumnModel().getColumn(i).setCellRenderer(new CustomRenderer());
            table1.getColumnModel().getColumn(i).setMaxWidth(20);
        }

        table1.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                lastEditedRow = -1;
                lastEditedCol = -1;
                super.mouseReleased(e);
            }
        });
        pixelCountLabel.setText(String.valueOf(table1.getColumnCount() * table1.getRowCount()));

        inputTextChanged();
    }

    private String gridToBinaryString() {
        StringBuilder str = new StringBuilder();
        for (int y = 0; y < table1.getRowCount(); y++) {
            for (int x = 0; x < table1.getColumnCount(); x++) {
                str.append(table1.getValueAt(y, x) == null ? 0 : 1);
            }
        }
        return str.toString();
    }

    private void updateTextField() {
        if(text2ImageCheckBox.isSelected()) return;

        textArea1.setText(currentEncoder.pixelChanged(gridToBinaryString()));
    }

    private void listSelectionModel(ListSelectionEvent e) {
        int row = table1.getSelectedRow();
        int col = table1.getSelectedColumn();
        if(row < 0 || col < 0) return;
        if(lastEditedRow == row && lastEditedCol == col) return;

        String a = (String) table1.getValueAt(row, col);
        if(a == null) table1.setValueAt("", row, col);
        else table1.setValueAt(null,row, col);

        updateTextField();

        lastEditedRow = row;
        lastEditedCol = col;
    }

    static class CustomRenderer extends DefaultTableCellRenderer {
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column)
        {
            JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            if(value == null) label.setBackground(Color.white);
            else label.setBackground(Color.black);
            return label;
        }
    }
}
