package ai.randomaccessguy.intelligpt;
import ai.randomaccessguy.intelligpt.ChatGPTClient;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.application.Application;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.ui.Messages;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.codeStyle.CodeStyleManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ChatGPTAction extends AnAction {

    public ChatGPTAction(){
        super();
    }

    /**
     * This constructor is used to support dynamically added menu actions.
     * It sets the text, description to be displayed for the menu item.
     * Otherwise, the default AnAction constructor is used by the IntelliJ Platform.
     *
     * @param text        The text to be displayed as a menu item.
     * @param description The description of the menu item.
     * @param icon        The icon to be used with the menu item.
     */
    public ChatGPTAction(@Nullable String text, @Nullable String description, @Nullable Icon icon) {
        super(text, description, icon);
    }

    @Override
    public void update(AnActionEvent e) {
        Editor editor = e.getData(CommonDataKeys.EDITOR);
        e.getPresentation().setEnabledAndVisible(true);
    }

    @Override
    public void actionPerformed(AnActionEvent e) {
        // Resto del codice del metodo actionPerformed
        Editor editor = e.getData(CommonDataKeys.EDITOR);
        if (editor != null) {

            String selectedText = editor.getSelectionModel().getSelectedText();
            if (selectedText != null) {
                String prompt = Messages.showInputDialog(e.getProject(), "Inserisci la richiesta:", "ChatGPT", Messages.getQuestionIcon());
                if (prompt != null && !prompt.isEmpty()) {

                    Runnable writeAction = () -> {

                        // Invia il testo selezionato a ChatGPT e ottieni la risposta
                        String generatedCode = generateCodeFromChatGPT(editor.getDocument().getText(), selectedText, prompt);

                        // Sostituisci il testo selezionato con il codice generato
                        replaceSelectedText(editor, generatedCode);
                    };

                    WriteCommandAction.runWriteCommandAction(e.getProject(), writeAction);
                }
            } else {

                String prompt = Messages.showInputDialog(e.getProject(), "Inserisci la richiesta:", "ChatGPT", Messages.getQuestionIcon());
                if (prompt != null && !prompt.isEmpty()) {

                    Runnable writeAction = () -> {

                        // Invia il testo selezionato a ChatGPT e ottieni la risposta
                        String generatedCode = generateCodeFromChatGPT(editor.getDocument().getText(), " ", prompt);

                        // Sostituisci il testo selezionato con il codice generato
                        generateText(editor, generatedCode);
                    };

                    WriteCommandAction.runWriteCommandAction(e.getProject(), writeAction);
                }
            }

        }
    }

    private String generateCodeFromChatGPT(String classCode, String selectedText, String prompt) {
        // Implementa la logica per inviare il testo selezionato a ChatGPT e ottenere la risposta
        // Utilizza il token OpenAI API per l'autenticazione
        // Restituisci il codice generato come stringa

        // Esempio di implementazione di un'interazione con l'API di ChatGPT
        ChatGPTClient client = new ChatGPTClient();
        String response = client.requestChatCompletion(classCode ,selectedText, prompt);

        // Estrarre il codice generato dalla risposta di ChatGPT
        String generatedCode = extractGeneratedCode(response);

        return generatedCode;
    }

    private String extractGeneratedCode(String selectedText) {
        String patternString = "```([\\s\\S]*?)```";
        Pattern pattern = Pattern.compile(patternString);
        Matcher matcher = pattern.matcher(selectedText);

        StringBuilder codeBuilder = new StringBuilder();
        while (matcher.find()) {
            String codeBlock = matcher.group(1);
            codeBuilder.append(codeBlock).append("\n");
        }

        String result = codeBuilder.toString().trim();

        result = result.replace("java", "");

        return result;
    }

    private void replaceSelectedText(Editor editor, String generatedCode) {
        editor.getDocument().replaceString(
                editor.getSelectionModel().getSelectionStart(),
                editor.getSelectionModel().getSelectionEnd(),
                generatedCode
        );

        // Indenta il codice sostituito
        CodeStyleManager.getInstance(editor.getProject()).reformatText(
                PsiDocumentManager.getInstance(editor.getProject()).getPsiFile(editor.getDocument()),
                editor.getSelectionModel().getSelectionStart(),
                editor.getSelectionModel().getSelectionStart() + generatedCode.length());
    }

    private void generateText(Editor editor, String generatedCode) {
        editor.getDocument().insertString(
                editor.getCaretModel().getCurrentCaret().getOffset(),
                generatedCode
        );

        // Indenta il codice sostituito
        CodeStyleManager.getInstance(editor.getProject()).reformatText(
                PsiDocumentManager.getInstance(editor.getProject()).getPsiFile(editor.getDocument()),
                editor.getCaretModel().getCurrentCaret().getOffset(),
                editor.getCaretModel().getCurrentCaret().getOffset() + generatedCode.length());
    }
}
