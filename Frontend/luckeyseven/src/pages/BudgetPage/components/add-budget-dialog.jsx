import React, { useState, useEffect } from 'react';
import axios from 'axios';
import '../styles/BudgetDialog.css';

const AddBudgetDialog = ({ teamId, budgetId, closeDialog, onBudgetUpdate }) => {
  const [additionalBudget, setAdditionalBudget] = useState(0);
  const [isExchanged, setIsExchanged] = useState(false);
  const [exchangeRate, setExchangeRate] = useState('');
  const [foreignCurrency, setForeignCurrency] = useState('KRW');
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [initialLoaded, setInitialLoaded] = useState(false);
  const [error, setError] = useState('');
  const [currentBudgetData, setCurrentBudgetData] = useState(null);

  useEffect(() => {
    const fetchBudget = async () => {
      try {
        // budgetId가 없을 경우, team의 예산을 fetch하려 시도
        const url = budgetId 
          ? `/api/teams/${teamId}/budget/${budgetId}`
          : `/api/teams/${teamId}/budget`;
          
        const response = await axios.get(url);
        const budget = response.data;
        
        setCurrentBudgetData(budget); // 기존 예산 데이터 저장
        setAdditionalBudget(0);
        setIsExchanged(!!budget.avgExchangeRate);
        setForeignCurrency(budget.foreignCurrency || 'KRW');
        setExchangeRate('');
        setInitialLoaded(true);
      } catch (error) {
        console.error('Error fetching budget:', error);
        // defaults data
        setAdditionalBudget(0);
        setIsExchanged(false);
        setExchangeRate('');
        setInitialLoaded(true);
        
        if (error.response && error.response.status === 404) {
          setError('예산 정보를 찾을 수 없습니다. 먼저 예산을 설정해주세요.');
        } else {
          setError('예산 정보를 불러오는 중 오류가 발생했습니다.');
        }
      }
    };
    
    fetchBudget();
  }, [teamId, budgetId]);

  const resetForm = () => {
    setAdditionalBudget(0);
    setIsExchanged(false);
    setExchangeRate('');
    setError('');
  };

  const handleClose = () => {
    resetForm();
    closeDialog();
  };

  const handleSubmit = async () => {
    if (isSubmitting) return;
    setIsSubmitting(true);
    setError('');

    // 입력값 유효성 검사
    if (additionalBudget <= 0) {
      setError('금액은 0보다 커야 합니다.');
      setIsSubmitting(false);
      return;
    }

    if (isExchanged && (!exchangeRate || exchangeRate <= 0)) {
      setError('유효한 환율을 입력해주세요.');
      setIsSubmitting(false);
      return;
    }

    try {
      const response = await axios.patch(`/api/teams/${teamId}/budget`, {
        additionalBudget: Number(additionalBudget),
        isExchanged,
        exchangeRate: isExchanged ? Number(exchangeRate) : null,
      });
      
      console.log('Budget update response:', response.data);
      
      if (onBudgetUpdate) {
        onBudgetUpdate(response.data);
      }
      
      resetForm();
      closeDialog();
    } catch (error) {
      console.error('Error updating budget:', error);
      
      if (error.response) {
        if (error.response.status === 404) {
          setError('예산 정보를 찾을 수 없습니다. 먼저 예산을 설정해주세요.');
        } else {
          setError('예산 수정 중 오류가 발생했습니다: ' + (error.response.data?.message || error.message));
        }
      } else {
        setError('서버와 통신 중 오류가 발생했습니다.');
      }
    } finally {
      setIsSubmitting(false);
    }
  };

  if (!initialLoaded) {
    return (
      <div className="modal-overlay">
        <div className="modal">
          <h2>예산 정보 로딩 중...</h2>
        </div>
      </div>
    );
  }

  // 예산 정보를 불러올 수 없는 경우 에러 메시지 표시
  if (error && !currentBudgetData) {
    return (
      <div className="modal-overlay" onClick={handleClose}>
        <div className="modal" onClick={(e) => e.stopPropagation()}>
          <h2>예산 수정</h2>
          <div className="error-message">{error}</div>
          <div className="modal-buttons">
            <button onClick={handleClose}>닫기</button>
          </div>
        </div>
      </div>
    );
  }

  return (
    <div className="modal-overlay" onClick={handleClose}>
      <div className="modal" onClick={(e) => e.stopPropagation()}>
        <h2>예산 추가</h2>

        {error && <div className="error-message">{error}</div>}
        
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