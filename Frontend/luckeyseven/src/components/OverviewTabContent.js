import React, { useState, useEffect, useRef } from 'react';
import styles from '../styles/App.module.css';
import SummaryCard from '../components/SummaryCard';
import BudgetBreakdown from '../components/BudgetBreakdown';
import RecentExpensesTable from '../components/RecentExpenseTable';

const OverviewTabContent = ({ dashboardData }) => {
  // 모든 React Hooks는 컴포넌트의 최상위 레벨에서 호출되어야 함
  const [savedTotalExpense, setSavedTotalExpense] = useState(0);
  const initializedRef = useRef(false);

  // useEffect도 최상위 레벨에서 호출
  useEffect(() => {
    // dashboardData가 없으면 아무것도 하지 않음
    if (!dashboardData) return;

    const { expenseList = [] } = dashboardData;
    
    // 아직 초기화되지 않았고, expenseList가 있는 경우에만 실행
    if (!initializedRef.current && expenseList && expenseList.length > 0) {
      // expenseList에서 총 지출 계산
      const calculatedExpense = expenseList.reduce((sum, expense) => 
        sum + parseFloat(expense.amount || 0), 0);
      
      // 계산된 값이 0보다 크면 저장
      if (calculatedExpense > 0) {
        setSavedTotalExpense(calculatedExpense);
        initializedRef.current = true;
      }
    }
  }, [dashboardData]); // dashboardData가 변경될 때만 실행

  // 조기 반환은 모든 Hooks가 호출된 후에 수행
  if (!dashboardData) {
    return <div>Loading overview...</div>;
  }

  const {
    // 사용하지 않는 변수들은 제거하거나 필요하면 유지할 수 있음
    // team_id,
    // teamName,
    totalAmount = 0, // 기본값 설정
    balance = 0, // 기본값 설정
    foreignBalance = 0, // 기본값 설정
    foreignCurrency = 'KRW', // 기본값 설정
    expenseList = [], // 기본값 설정
    // avgExchangeRate = 0, // 기본값 설정
  } = dashboardData;

  // savedTotalExpense를 사용하여 퍼센티지 계산
  const totalExpensePercentage = totalAmount > 0 ? (savedTotalExpense / totalAmount) * 100 : 0;
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
        <SummaryCard title="총 예산" amount={totalAmount} currency="₩" />
        <SummaryCard title="총 지출" amount={savedTotalExpense} currency="₩" percentage={totalExpensePercentage.toFixed(1)} of="of budget" />
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