import React from 'react';
import styles from '../styles/App.module.css'; // Keep App.module.css for general layout styles
import SummaryCard from '../components/SummaryCard';
import BudgetBreakdown from '../components/BudgetBreakdown';
import RecentExpensesTable from '../components/RecentExpenseTable';

const OverviewTabContent = ({ dashboardData }) => {
  if (!dashboardData) {
    return <div>Loading overview...</div>; // Or some other loading indicator
  }

  // Adapt data for child components
  const {
    team_id,
    totalAmount,
    balance,
    foreignBalance,
    foreignCurrency, 
    expenseList, // This is List<ExpenseDto>
    avgExchangeRate
  } = dashboardData;

  // Calculate percentages for SummaryCard if needed
  const totalExpense = totalAmount - balance;
  const totalExpensePercentage = totalAmount > 0 ? (totalExpense / totalAmount) * 100 : 0;
  const remainingBudgetPercentage = totalAmount > 0 ? (balance / totalAmount) * 100 : 0;
  const transformedExpenses = expenseList.map(expense => ({
    id: expense.id,
    title: `Expense by ${expense.payerNickname}`, 
    amount: parseFloat(expense.amount), 
    category: expense.category,
    description: expense.description,
    date: expense.date, 
    paidBy: expense.payerNickname,
    paymentMethod: expense.paymentMethod, 
    currency: expense.paymentMethod == "CASH" ?  '₩': foreignCurrency, // Assuming KRW for now, adjust if dynamic
  }));


  return (
    <div>
      <div className={styles.summaryCardContainer}>
        <SummaryCard title="총 예산" amount={totalAmount} currency="₩"/>
        <SummaryCard title="총 지출" amount={totalExpense} currency="₩" percentage={totalExpensePercentage.toFixed(1)} of="of budget" />
        <SummaryCard title="남은 예산" amount={balance} currency="₩" percentage={remainingBudgetPercentage.toFixed(1)} of="of budget" />
        {foreignCurrency && foreignBalance !== undefined && (
          <SummaryCard
            title={`남은 외화 (${foreignCurrency})`}
            amount={foreignBalance}
            currency={foreignCurrency} // Use the currency code (e.g., USD, JPY)
            // percentage={...} // Add percentage if applicable
            // of="of foreign budget"
          />
        )}
      </div>
      <div className={styles.detailsContainer}>
        {/* BudgetBreakdown might need significant changes based on how categories are handled now */}
        <BudgetBreakdown expenses={transformedExpenses} />
        <RecentExpensesTable expenses={transformedExpenses} />
      </div>
    </div>
  );
};

export default OverviewTabContent;
