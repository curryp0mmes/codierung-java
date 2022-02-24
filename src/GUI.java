import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.border.LineBorder;
import javax.swing.event.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;
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

        updateTable();


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
        });

        spinnerRow.addChangeListener(e -> updateTable());
        spinnerCol.addChangeListener(e -> updateTable());

        textArea1.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                System.out.println("insert");
                symbolCountLabel.setText(String.valueOf(textArea1.getText().length()));
                if(text2ImageCheckBox.isSelected()) currentEncoder.textChanged(textArea1.getText());
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                System.out.println("remove");
                symbolCountLabel.setText(String.valueOf(textArea1.getText().length()));
                if(text2ImageCheckBox.isSelected()) currentEncoder.textChanged(textArea1.getText());
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                System.out.println("change");
                symbolCountLabel.setText(String.valueOf(textArea1.getText().length()));
                if(text2ImageCheckBox.isSelected()) currentEncoder.textChanged(textArea1.getText());
            }
        });
    }

    private void updateTable() {
        table1.setModel( new DefaultTableModel( (Integer)spinnerRow.getValue(), (Integer)spinnerCol.getValue() ) );
        table1.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        //table1.setDefaultEditor(Object.class, new CustomEditor());

        table1.getSelectionModel().addListSelectionListener(this::listSelectionModel);
        table1.getColumnModel().getSelectionModel().addListSelectionListener(this::listSelectionModel);

        for (int i = 0; i < table1.getColumnCount(); i++) {
            table1.getColumnModel().getColumn(i).setCellRenderer(new CustomRenderer());
        }

        table1.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                lastEditedRow = -1;
                lastEditedCol = -1;
                textArea1.setText("Test");
                super.mouseReleased(e);
            }
        });
    }

    private String gridToBinaryString() {
        StringBuilder str = new StringBuilder();
        for (int x = 0; x < table1.getColumnCount(); x++) {
            for (int y = 0; y < table1.getRowCount(); y++) {
                str.append(table1.getValueAt(x, y));
            }
        }
        return str.toString();
    }

    private void listSelectionModel(ListSelectionEvent e) {
        System.out.println("Event " + e.getSource());
        int row = table1.getSelectedRow();
        int col = table1.getSelectedColumn();
        if(row < 0 || col < 0) return;
        if(lastEditedRow == row && lastEditedCol == col) return;

        String a = (String) table1.getValueAt(row, col);
        if(a == null) table1.setValueAt("", row, col);
        else table1.setValueAt(null,row, col);

        lastEditedRow = row;
        lastEditedCol = col;
    }

    public static class CustomEditor extends DefaultCellEditor {
        public CustomEditor() {
            super(new JTextField());
        }

        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            return null;
        }
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
