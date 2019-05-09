import dataModel.LogManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class StatsController {

  @FXML private DatePicker timePicker1, timePicker2;
  @FXML private ListView<String> ingredientLV, dishLV;
  @FXML private Label errorInfo;
  @FXML private LineChart<String, Number> moneyC;
  @FXML private Label incomeT, earningT, expenseT;
  private LogManager logManager = Main.restaurant.getLogManager();

  @FXML
  private void toManaging(ActionEvent event) throws IOException {
    Parent root = FXMLLoader.load(getClass().getResource("managing.fxml"));
    Scene menuView = new Scene(root);
    Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
    window.setScene(menuView);
    window.show();
  }

  @FXML
  private void search() {
    moneyC.getData().remove(0, moneyC.getData().size());
    LocalDate date1 = timePicker1.getValue();
    LocalDate date2 = timePicker2.getValue();
    if (date1 == null || date2 == null || date2.isBefore(date1)) {
      errorInfo.setText("Date empty or invalid");
      return;
    } else {
      dishLV.setItems(logManager.getDishStats(date1, date2));
      ingredientLV.setItems(logManager.getIngredientStats(date1, date2));
      moneyC.getData().addAll(getChart(date1, date2));
    }
    timePicker1.setValue(null);
    timePicker2.setValue(null);
  }

  /** A helper function to get the chart data */
  private List<XYChart.Series<String, Number>> getChart(LocalDate date1, LocalDate date2) {
    List<XYChart.Series<String, Number>> data = new ArrayList<>();
    XYChart.Series<String, Number> income = new XYChart.Series<>();
    income.setName("Income");
    XYChart.Series<String, Number> earning = new XYChart.Series<>();
    earning.setName("Earning");
    double incomeMoney = 0;
    double expenseMoney = 0;
    double earningMoney = 0;
    double dailyIncome;
    double dailyExpenses;
    while (date1.isBefore(date2) || date1.equals(date2)) {
      String date = date1.format(DateTimeFormatter.ofPattern("MM-dd"));
      dailyIncome = logManager.getIncome(date1);
      dailyExpenses = logManager.getExpenses(date1);
      income.getData().add(new XYChart.Data<>(date, dailyIncome));
      incomeMoney += dailyIncome;
      earning.getData().add(new XYChart.Data<>(date, dailyExpenses));
      expenseMoney += dailyExpenses;
      earningMoney += dailyIncome + dailyExpenses;
      date1 = date1.plusDays(1);
    }
    incomeT.setText(String.valueOf(Math.round(incomeMoney * 100) / 100.0));
    expenseT.setText(String.valueOf(Math.round(expenseMoney * 100) / 100.0));
    if (earningMoney < 0) {
      earningT.setTextFill(Color.RED);
    } else {
      earningT.setTextFill(Color.GREEN);
    }
    earningT.setText(String.valueOf(Math.round(earningMoney * 100) / 100.0));
    data.add(income);
    data.add(earning);
    return data;
  }
}
