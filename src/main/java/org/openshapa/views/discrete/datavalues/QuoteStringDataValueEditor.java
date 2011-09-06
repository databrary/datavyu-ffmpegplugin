/**
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.openshapa.views.discrete.datavalues;

import javax.swing.text.JTextComponent;

import database.DataCell;
import database.Matrix;
import database.PredDataValue;
import database.QuoteStringDataValue;
import org.openshapa.views.discrete.EditorComponent;

/**
 * This class is the character editor of a QuoteStringDataValue.
 */
public final class QuoteStringDataValueEditor extends DataValueEditor {

    private MatrixRootView matrixRootView;
    private EditorComponent leftQuote = null;
    private EditorComponent rightQuote = null;

    /**
     * Constructor.
     * TODO: QuoteString is coupled to MatrixRootView which shoule be
     * refactored somehow so we can use editors in other JTextComponents.
     * See the cast below - not good.
     *
     * @param ta The parent JTextComponent the editor is in.
     * @param cell The parent data cell this editor resides within.
     * @param matrix Matrix holding the datavalue this editor will represent.
     * @param matrixIndex The index of the datavalue within the matrix.
     * @param leftq EditorComponent of the left quote fixed text.
     * @param rightq EditorComponent of the right quote fixed text.
     */
    public QuoteStringDataValueEditor(final JTextComponent ta,
                            final DataCell cell,
                            final Matrix matrix,
                            final int matrixIndex,
                            final EditorComponent leftq,
                            final EditorComponent rightq) {
        super(ta, cell, matrix, matrixIndex);
        initValue(ta, leftq, rightq);
    }

    /**
     * Constructor.
     *
     * @param ta The parent JTextComponent the editor is in.
     * @param cell The parent data cell this editor resides within.
     * @param p The predicate holding the datavalue this editor will represent.
     * @param pi The index of the datavalue within the predicate.
     * @param matrix Matrix holding the datavalue this editor will represent.
     * @param matrixIndex The index of the datavalue within the matrix.
     * @param leftq EditorComponent of the left quote fixed text.
     * @param rightq EditorComponent of the right quote fixed text.
     */
    public QuoteStringDataValueEditor(final JTextComponent ta,
                            final DataCell cell,
                            final PredDataValue p,
                            final int pi,
                            final Matrix matrix,
                            final int matrixIndex,
                            final EditorComponent leftq,
                            final EditorComponent rightq) {
        super(ta, cell, p, pi, matrix, matrixIndex);
        initValue(ta, leftq, rightq);
    }

    /**
     * Initialise the remaining elements of the editor.
     */
    private void initValue(final JTextComponent ta,
                          final EditorComponent leftq,
                          final EditorComponent rightq) {
        setAcceptReturnKey(true);
        matrixRootView = (MatrixRootView) ta;
        leftQuote = leftq;
        rightQuote = rightq;
        initQuotes();
    }

    /**
     * Recalculate the string for this editor.
     * Overrides to handle the extra quote character fixed texts.
     */
    @Override
    public void updateStrings() {
        super.updateStrings();
        checkQuotes();
    }

    /**
     * Modify the text of the quote fixed texts if we are a null arg.
     */
    private void checkQuotes() {
        QuoteStringDataValue qsdv = (QuoteStringDataValue) getModel();
        if (leftQuote != null && rightQuote != null) {
            if (!qsdv.isEmpty()) {
                leftQuote.setText("\"");
                rightQuote.setText("\"");
                String t = getText();
                this.resetText(t.substring(1, t.length() - 1));
            } else {
                leftQuote.setText("");
                rightQuote.setText("");
            }
            matrixRootView.rebuildText();
        }
    }

    /**
     * Modify the text of the quote fixed texts if we are a null arg.
     * But do not cause MatrixRootView to be called.
     */
    private void initQuotes() {
        QuoteStringDataValue qsdv = (QuoteStringDataValue) getModel();
        if (leftQuote != null && rightQuote != null) {
            if (!qsdv.isEmpty()) {
                leftQuote.resetText("\"");
                rightQuote.resetText("\"");
                String t = getText();
                this.resetText(t.substring(1, t.length() - 1));
            } else {
                leftQuote.resetText("");
                rightQuote.resetText("");
            }
        }
    }
}