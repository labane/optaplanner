/*
 * Copyright 2015 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.optaplanner.examples.investmentallocation.swingui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.util.List;
import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.SwingConstants;

import org.optaplanner.core.api.domain.solution.Solution;
import org.optaplanner.examples.common.swingui.SolutionPanel;
import org.optaplanner.examples.common.swingui.TangoColorFactory;
import org.optaplanner.examples.common.swingui.components.LabeledComboBoxRenderer;
import org.optaplanner.examples.common.swingui.timetable.TimeTablePanel;
import org.optaplanner.examples.investmentallocation.domain.AssetClass;
import org.optaplanner.examples.investmentallocation.domain.InvestmentAllocationSolution;

import static org.optaplanner.examples.common.swingui.timetable.TimeTablePanel.HeaderColumnKey.*;
import static org.optaplanner.examples.common.swingui.timetable.TimeTablePanel.HeaderRowKey.*;

public class InvestmentAllocationPanel extends SolutionPanel {

    public static final String LOGO_PATH = "/org/optaplanner/examples/investmentallocation/swingui/investmentAllocationLogo.png";

    private final TimeTablePanel<AssetClass, AssetClass> correlationPanel;


    public InvestmentAllocationPanel() {
        setLayout(new BorderLayout());
        JTabbedPane tabbedPane = new JTabbedPane();
        correlationPanel = new TimeTablePanel<AssetClass, AssetClass>();
        tabbedPane.add("Correlation", new JScrollPane(correlationPanel));
        add(tabbedPane, BorderLayout.CENTER);
        setPreferredSize(PREFERRED_SCROLLABLE_VIEWPORT_SIZE);
    }

    @Override
    public boolean isWrapInScrollPane() {
        return false;
    }

    @Override
    public boolean isRefreshScreenDuringSolving() {
        return true;
    }

    private InvestmentAllocationSolution getInvestmentAllocationSolution() {
        return (InvestmentAllocationSolution) solutionBusiness.getSolution();
    }

    public void resetPanel(Solution s) {
        correlationPanel.reset();
        InvestmentAllocationSolution solution = (InvestmentAllocationSolution) s;
        defineGrid(solution);
        fillCells(solution);
        repaint(); // Hack to force a repaint of TimeTableLayout during "refresh screen while solving"
    }

    private void defineGrid(InvestmentAllocationSolution solution) {
        JButton footprint = new JButton("99999999");
        footprint.setMargin(new Insets(0, 0, 0, 0));
        int footprintWidth = footprint.getPreferredSize().width;

        correlationPanel.defineColumnHeaderByKey(HEADER_COLUMN); // Period header
        for (AssetClass assetClass : solution.getAssetClassList()) {
            correlationPanel.defineColumnHeader(assetClass, footprintWidth);
        }
        correlationPanel.defineColumnHeader(null, footprintWidth); // Unassigned

        correlationPanel.defineRowHeaderByKey(HEADER_ROW); // Room header
        for (AssetClass assetClass : solution.getAssetClassList()) {
            correlationPanel.defineRowHeader(assetClass);
        }
        correlationPanel.defineRowHeader(null); // Unassigned period
    }

    private void fillCells(InvestmentAllocationSolution solution) {
        correlationPanel.addCornerHeader(HEADER_COLUMN, HEADER_ROW, createHeaderPanel(new JLabel("Asset class"), null));
        for (AssetClass assetClass : solution.getAssetClassList()) {
            correlationPanel.addColumnHeader(assetClass, HEADER_ROW,
                    createHeaderPanel(new JLabel(assetClass.getName(), SwingConstants.CENTER),
                            "Expected return: " + assetClass.getExpectedReturnLabel()
                            + "\nStandard deviation risk: " + assetClass.getStandardDeviationRiskLabel()));
        }
        for (AssetClass assetClass : solution.getAssetClassList()) {
            correlationPanel.addRowHeader(HEADER_COLUMN, assetClass,
                    createHeaderPanel(new JLabel(assetClass.getName(), SwingConstants.CENTER),
                            "Expected return: " + assetClass.getExpectedReturnLabel()
                            + "\nStandard deviation risk: " + assetClass.getStandardDeviationRiskLabel()));
        }
        for (AssetClass a : solution.getAssetClassList()) {
            for (AssetClass b : solution.getAssetClassList()) {
                correlationPanel.addCell(a, b, new JLabel(a.getCorrelationLabel(b)));
            }
        }
    }

    private JPanel createHeaderPanel(JLabel label, String toolTipText) {
        if (toolTipText != null) {
            label.setToolTipText(toolTipText);
        }
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.add(label, BorderLayout.NORTH);
        headerPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(TangoColorFactory.ALUMINIUM_5),
                BorderFactory.createEmptyBorder(2, 2, 2, 2)));
        return headerPanel;
    }

}