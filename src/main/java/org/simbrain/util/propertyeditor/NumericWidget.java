package org.simbrain.util.propertyeditor;

import org.simbrain.util.Parameter;
import org.simbrain.util.ResourceManager;
import org.simbrain.util.StandardDialog;
import org.simbrain.util.math.ProbabilityDistribution;
import org.simbrain.util.widgets.JNumberSpinnerWithNull;
import org.simbrain.util.widgets.SpinnerNumberModelWithNull;

import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * Used in the {@link org.simbrain.util.widgets.ParameterWidget} for numeric
 * fields. Has an an "up / down" spinner and the option of a randomization
 * button.
 */
public class NumericWidget extends JPanel {

    /**
     * The spinner component.
     */
    private JNumberSpinnerWithNull spinner;

    /**
     * The randomization button.
     */
    private JButton randomizeButton = new JButton(ResourceManager.getImageIcon("Rand.png"));

    /**
     * Construct a numeric widget.
     *
     * @param editableObjects the objects being edited
     * @param parameter       the parameter field
     * @param spinnerModel    the spinner model
     * @param setNull         a function to set the field to null (inconsistent) when the randomize button is clicked
     */
    public NumericWidget(
            List<? extends EditableObject> editableObjects,
            Parameter parameter,
            SpinnerNumberModelWithNull spinnerModel,
            Runnable setNull
    ) {
        super();
        setLayout(new GridBagLayout());
        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;

        spinner = new JNumberSpinnerWithNull(spinnerModel);
        gridBagConstraints.weightx = 10;
        add(spinner, gridBagConstraints);

        randomizeButton.setToolTipText("Randomize this parameter. Note that upon pressing OK the value" +
            " will be updated immediately.");
        String probDist =parameter.getAnnotation().probDist();

        // Handle randomizer button
        if (!probDist.isEmpty()) {
            randomizeButton.addActionListener((evt) -> {

                double param1 = parameter.getAnnotation().probParam1();
                double param2 = parameter.getAnnotation().probParam2();
                ProbabilityDistribution.ProbabilityDistributionBuilder pb;
                if(parameter.hasMaxValue() || parameter.hasMinValue()) {
                    double upBound = parameter.hasMaxValue() ?
                            parameter.getAnnotation().maximumValue() : Double.POSITIVE_INFINITY;
                    double lowBound = parameter.hasMinValue() ?
                            parameter.getAnnotation().minimumValue() : Double.NEGATIVE_INFINITY;
                    pb = ProbabilityDistribution.getBuilder(probDist, param1,param2, lowBound, upBound);
                } else {
                    pb = ProbabilityDistribution.getBuilder(probDist, param1, param2);
                }
                ProbabilityDistribution pd = pb.build();

                AnnotatedPropertyEditor randEditor = new AnnotatedPropertyEditor(new ProbabilityDistribution.Randomizer(pd));
                StandardDialog dialog = new StandardDialog();
                dialog.setContentPane(randEditor);
                dialog.pack();

                dialog.addClosingTask(() -> {
                    editableObjects.forEach(o -> {
                        if (parameter.isNumericInteger()) {
                            parameter.setFieldValue(o, pd.nextRandInt());
                        } else {
                            parameter.setFieldValue(o, pd.nextRand());
                        }
                    });
                    // Provides some indication that fields have been mutated
                    if (setNull != null) {
                        setNull.run();
                    }
                });

                dialog.setLocationRelativeTo(null);
                dialog.setVisible(true);
                dialog.isAlwaysOnTop();


            });
            gridBagConstraints.weightx = 2;
            add(randomizeButton, gridBagConstraints);
        }
    }

    public Object getValue() {
        return spinner.getValue();
    }

    public void setValue(Object value) {
        spinner.setValue(value);
    }


}
