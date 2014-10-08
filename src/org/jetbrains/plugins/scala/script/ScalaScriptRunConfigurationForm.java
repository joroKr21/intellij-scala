package org.jetbrains.plugins.scala.script;

import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.ui.RawCommandLineEditor;
import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;

import javax.swing.*;
import java.awt.*;

/**
 * User: Alexander Podkhalyuzin
 * Date: 05.02.2009
 */
public class ScalaScriptRunConfigurationForm {
    private ScalaScriptRunConfiguration myConfiguration;
    private Project myProject;
    private JPanel myPanel;
    private TextFieldWithBrowseButton textFieldWithBrowseButton1;
    private RawCommandLineEditor scriptArgsEditor;
    private RawCommandLineEditor javaOptionsEditor;
    private TextFieldWithBrowseButton workingDirectoryField;
    private RawCommandLineEditor consoleArgsEditor;

    public ScalaScriptRunConfigurationForm(final Project project, final ScalaScriptRunConfiguration configuration) {
        myProject = project;
        myConfiguration = configuration;
        addFileChooser("Select scala script file", textFieldWithBrowseButton1, project, false);
        addFileChooser("Choose Working Directory", workingDirectoryField, project, true);
        scriptArgsEditor.setName("Scala script program arguments");
        scriptArgsEditor.setDialogCaption("Scala script program arguments editor");
        javaOptionsEditor.setName("VM options");
        javaOptionsEditor.setDialogCaption("VM opotions editor");
        VirtualFile baseDir = project.getBaseDir();
        consoleArgsEditor.setName("Console Arguments");
        consoleArgsEditor.setDialogCaption("Scala script console arguments editor");
        String path = baseDir != null ? baseDir.getPath() : "";
        workingDirectoryField.setText(path);
    }

    public JPanel getPanel() {
        return myPanel;
    }

    public String getScriptPath() {
        return textFieldWithBrowseButton1.getText();
    }

    public void setScriptPath(String scriptPath) {
        textFieldWithBrowseButton1.setText(scriptPath);
    }

    public String getScriptArgs() {
        return scriptArgsEditor.getText();
    }

    public void setScriptArgs(String scriptArgs) {
        scriptArgsEditor.setText(scriptArgs);
    }

    public String getJavaOptions() {
        return javaOptionsEditor.getText();
    }

    public void setJavaOptions(String s) {
        javaOptionsEditor.setText(s);
    }

    public String getConsoleArgs() {
        return consoleArgsEditor.getText();
    }

    public void setConsoleArgs(String args) {
        this.consoleArgsEditor.setText(args);
    }

    public void apply(ScalaScriptRunConfiguration configuration) {
        setScriptArgs(configuration.getScriptArgs());
        setScriptPath(configuration.getScriptPath());
        setJavaOptions(configuration.getJavaOptions());
        setConsoleArgs(configuration.getConsoleArgs());
        setWorkingDirectory(configuration.getWorkingDirectory());
    }

    public String getWorkingDirectory() {
        return workingDirectoryField.getText();
    }

    public void setWorkingDirectory(String s) {
        workingDirectoryField.setText(s);
    }

    private FileChooserDescriptor addFileChooser(final String title,
                                                 final TextFieldWithBrowseButton textField,
                                                 final Project project, final Boolean directories) {
        final FileChooserDescriptor fileChooserDescriptor = new FileChooserDescriptor(!directories, directories, false, false, false, false) {
            @Override
            public boolean isFileVisible(VirtualFile file, boolean showHiddenFiles) {
                return super.isFileVisible(file, showHiddenFiles) && (file.isDirectory() || (!directories && "scala".equals(file.getExtension())));
            }
        };
        fileChooserDescriptor.setTitle(title);
        textField.addBrowseFolderListener(title, null, project, fileChooserDescriptor);
        return fileChooserDescriptor;
    }

    {
// GUI initializer generated by IntelliJ IDEA GUI Designer
// >>> IMPORTANT!! <<<
// DO NOT EDIT OR ADD ANY CODE HERE!
        $$$setupUI$$$();
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        myPanel = new JPanel();
        myPanel.setLayout(new GridLayoutManager(11, 1, new Insets(0, 0, 0, 0), -1, -1));
        textFieldWithBrowseButton1 = new TextFieldWithBrowseButton();
        myPanel.add(textFieldWithBrowseButton1, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        scriptArgsEditor = new RawCommandLineEditor();
        myPanel.add(scriptArgsEditor, new GridConstraints(3, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final Spacer spacer1 = new Spacer();
        myPanel.add(spacer1, new GridConstraints(10, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        final JLabel label1 = new JLabel();
        label1.setText("Program parameters:");
        myPanel.add(label1, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label2 = new JLabel();
        label2.setText("Script file:");
        myPanel.add(label2, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label3 = new JLabel();
        label3.setText("VM options:");
        myPanel.add(label3, new GridConstraints(4, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        javaOptionsEditor = new RawCommandLineEditor();
        myPanel.add(javaOptionsEditor, new GridConstraints(5, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JLabel label4 = new JLabel();
        label4.setText("Working directory:");
        myPanel.add(label4, new GridConstraints(8, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        workingDirectoryField = new TextFieldWithBrowseButton();
        myPanel.add(workingDirectoryField, new GridConstraints(9, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JLabel label5 = new JLabel();
        label5.setText("Script Runner Args (e.g. -deprecation):");
        myPanel.add(label5, new GridConstraints(6, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        consoleArgsEditor = new RawCommandLineEditor();
        myPanel.add(consoleArgsEditor, new GridConstraints(7, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return myPanel;
    }
}
