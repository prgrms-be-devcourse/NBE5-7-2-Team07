import React, { useState } from 'react';
import axios from 'axios';
import '../styles/BudgetDialog.css';

const AddBudgetDialog = ({ teamId, closeDialog, onBudgetUpdate }) => {
  const [additionalBudget, setAdditionalBudget] = useState(0);
  const [isExchanged, setIsExchanged] = useState(false);
  const [exchangeRate, setExchangeRate] = useState('');
  const [isSubmitting, setIsSubmitting] = useState(false);

  const resetForm = () => {
    setAdditionalBudget(0);
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
        additionalBudget,
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
      console.error('Error adding budget:', error);
      alert('예산 추가 중 오류가 발생했습니다: ' + (error.response?.data?.message || error.message));
    } finally {
      setIsSubmitting(false);
    }
  };

  return (
    <div className="modal-overlay" onClick={handleClose}>
      <div className="modal" onClick={(e) => e.stopPropagation()}>
        <h2>예산 추가</h2>
        <label>추가 예산 금액</label>
        <input
          type="number"
          value={additionalBudget}
          onChange={(e) => setAdditionalBudget(e.target.value)}
          placeholder="추가할 예산 금액"
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
            {isSubmitting ? '처리 중...' : '예산 추가'}
          </button>
        </div>
      </div>
    </div>
  );
};

export default AddBudgetDialog;