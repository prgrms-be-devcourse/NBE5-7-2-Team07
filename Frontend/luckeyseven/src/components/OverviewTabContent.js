import React from 'react';
import styles from '../styles/App.module.css';
import SummaryCard from '../components/SummaryCard';
import BudgetBreakdown from '../components/BudgetBreakdown';
import RecentExpensesTable from '../components/RecentExpenseTable';

const OverviewTabContent = ({ dashboardData }) => {
  if (!dashboardData) {
    return <div>Loading overview...</div>;
  }

  const {
    team_id,
    teamName,
    totalAmount = 0, // 기본값 설정
    balance = 0, // 기본값 설정
    foreignBalance = 0, // 기본값 설정
    foreignCurrency = 'KRW', // 기본값 설정
    expenseList = [], // 기본값 설정
    avgExchangeRate = 0, // 기본값 설정
  } = dashboardData;

  const totalExpense = totalAmount - balance;
  const totalExpensePercentage = totalAmount > 0 ? (totalExpense / totalAmount) * 100 : 0;
  const remainingBudgetPercentage = totalAmount > 0 ? (balance / totalAmount) * 100 : 0;
  
  // 지출 목록이 없는 경우 빈 배열로 처리
  const transformedExpenses = Array.isArray(expenseList) ? expenseList.map(expense => ({
    id: expense.id,
    title: `Expense by ${expense.payerNickname}`,
    amount: parseFloat(expense.amount),
    category: expense.category,
    description: expense.description,
    date: expense.date,
    paidBy: expense.payerNickname,
    paymentMethod: expense.paymentMethod,
    currency: expense.paymentMethod === "CASH" ? '₩' : foreignCurrency,
  })) : [];

  return (
    <div>
      <div className={styles.summaryCardContainer}>
        <SummaryCard title="총 예산" amount={totalAmount} currency="₩"/>
        <SummaryCard title="총 지출" amount={totalExpense} currency="₩" percentage={totalExpensePercentage.toFixed(1)} of="of budget" />
        <SummaryCard title="남은 예산" amount={balance} currency="₩" percentage={remainingBudgetPercentage.toFixed(1)} of="of budget" />
        {foreignCurrency && foreignBalance !== undefined && foreignCurrency !== 'KRW' && (
          <SummaryCard
            title={`남은 외화 (${foreignCurrency})`}
            amount={foreignBalance}
            currency={foreignCurrency}
          />
        )}
      </div>
      <div className={styles.detailsContainer}>
        <BudgetBreakdown expenses={transformedExpenses} />
        <RecentExpensesTable expenses={transformedExpenses} />
      </div>
    </div>
  );
};

export default OverviewTabContent;