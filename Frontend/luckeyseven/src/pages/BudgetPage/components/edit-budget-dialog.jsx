import React, { useState, useEffect } from 'react';
import axios from 'axios';
import '../styles/BudgetDialog.css';

const EditBudgetDialog = ({ teamId, budgetId, closeDialog, onBudgetUpdate }) => {
  const [totalAmount, setTotalAmount] = useState(0);
  const [isExchanged, setIsExchanged] = useState(false);
  const [exchangeRate, setExchangeRate] = useState('');
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [initialLoaded, setInitialLoaded] = useState(false);

  useEffect(() => {
    const fetchBudget = async () => {
      try {
        // If budgetId is not provided, try to fetch the team's budget
        const url = budgetId 
          ? `/api/teams/${teamId}/budget/${budgetId}`
          : `/api/teams/${teamId}/budget`;
          
        const response = await axios.get(url);
        const budget = response.data;
        setTotalAmount(budget.balance || budget.totalAmount || 0);
        setIsExchanged(!!budget.isExchanged);
        setExchangeRate(budget.avgExchangeRate || '');
        setInitialLoaded(true);
      } catch (error) {
        console.error('Error fetching budget:', error);
        // If we can't fetch the budget data, set defaults
        setTotalAmount(0);
        setIsExchanged(false);
        setExchangeRate('');
        setInitialLoaded(true);
      }
    };
    
    fetchBudget();
  }, [teamId, budgetId]);

  const resetForm = () => {
    setTotalAmount(0);
    setIsExchanged(false);
    setExchangeRate('');
  };

  const handleClose = () => {
    resetForm();
    closeDialog();
  };

  const handleSubmit = async () => {
    if (isSubmitting) return;
    setIsSubmitting(true);

    try {
      const response = await axios.patch(`/api/teams/${teamId}/budget`, {
        totalAmount,
        isExchanged,
        exchangeRate: isExchanged ? exchangeRate : null,
      });
      console.log(response.data);
      
      if (onBudgetUpdate) {
        onBudgetUpdate(response.data);
      }
      
      resetForm();
      closeDialog();
    } catch (error) {
      console.error('Error updating budget:', error);
      alert('예산 수정 중 오류가 발생했습니다: ' + (error.response?.data?.message || error.message));
    } finally {
      setIsSubmitting(false);
    }
  };

  // If we're still loading the initial data, show a loading indicator
  if (!initialLoaded) {
    return (
      <div className="modal-overlay">
        <div className="modal">
          <h2>예산 정보 로딩 중...</h2>
        </div>
      </div>
    );
  }

  return (
    <div className="modal-overlay" onClick={handleClose}>
      <div className="modal" onClick={(e) => e.stopPropagation()}>
        <h2>예산 수정</h2>
        <label>수정 예산 금액</label>
        <input
          type="number"
          value={totalAmount}
          onChange={(e) => setTotalAmount(e.target.value)}
          placeholder="수정할 예산 금액"
          min = "0"
          step = "100"
        />
        
        <div className="toggle-buttons">
          <label>환율 적용 여부</label>
          <button 
            className={isExchanged ? 'active' : ''} 
            onClick={() => setIsExchanged(true)}
          >
            예
          </button>
          <button 
            className={!isExchanged ? 'active' : ''} 
            onClick={() => setIsExchanged(false)}
          >
            아니오
          </button>
        </div>
        
        {isExchanged && (
          <>
            <label>환율</label>
            <input
              type="number"
              value={exchangeRate}
              onChange={(e) => setExchangeRate(e.target.value)}
              placeholder="환율"
              min = "0"
            />
          </>
        )}
        
        <div className="modal-buttons">
          <button onClick={handleClose}>취소</button>
          <button 
            className="primary" 
            onClick={handleSubmit}
            disabled={isSubmitting}
          >
            {isSubmitting ? '처리 중...' : '예산 수정'}
          </button>
        </div>
      </div>
    </div>
  );
};

export default EditBudgetDialog;