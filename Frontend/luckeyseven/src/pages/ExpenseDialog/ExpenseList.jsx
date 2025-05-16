import React, { useState, useEffect, useCallback } from 'react';
import AddExpenseDialog from './AddExpenseDialog';
import ExpenseDetailDialog from './ExpenseDetailDialog';
import '../../components/styles/expenseList.css';
import { getListExpense } from '../../service/ExpenseService';

const CATEGORY_LABELS = {
  MEAL: '식사',
  SNACK: '간식',
  TRANSPORT: '교통',
  ACCOMMODATION: '숙박',
  MISCELLANEOUS: '기타',
};

export default function ExpenseList({ teamId = 1 }) {
  const [showAddDialog, setShowAddDialog] = useState(false);
  const [selectedExpenseId, setSelectedExpenseId] = useState(null);

  // 페이징 상태
  const [expenses, setExpenses] = useState([]);
  const [page, setPage] = useState(0); // 0-based index
  const [size] = useState(10);
  const [totalPages, setTotalPages] = useState(1);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  // 서버에서 받은 잔고 배너 및 알림 상태
  const [balances, setBalances] = useState(null);
  const [notification, setNotification] = useState({ message: '', type: '' });

  // 지출 목록 재조회 함수
  const fetchExpenses = useCallback(async () => {
    setLoading(true);
    try {
      const data = await getListExpense(teamId, page, size);
      setExpenses(data.content);
      setTotalPages(data.totalPages);
      setError(null);
    } catch (err) {
      setError(err);
    } finally {
      setLoading(false);
    }
  }, [teamId, page, size]);

  // 초기 및 페이지 변경 시 데이터 로드
  useEffect(() => {
    fetchExpenses();
  }, [fetchExpenses]);

  // 배너 및 알림 자동 숨김 (10초)
  useEffect(() => {
    if (!balances && !notification.message) return;
    const timer = setTimeout(() => {
      setBalances(null);
      setNotification({ message: '', type: '' });
    }, 10000);
    return () => clearTimeout(timer);
  }, [balances, notification]);

  if (loading) return (
    <div className="expense-tracker">
      <div className="header">
        <h1 className="title">여행 경비 매니저</h1>
      </div>
      <div className="content">
        <div className="loading">데이터를 불러오고 있습니다...</div>
      </div>
    </div>
  );
  
  if (error) return (
    <div className="expense-tracker">
      <div className="header">
        <h1 className="title">여행 경비 매니저</h1>
      </div>
      <div className="content">
        <div className="error">
          <p>데이터를 불러오는 중 오류가 발생했습니다</p>
          <p>{error.message}</p>
        </div>
      </div>
    </div>
  );

  const openDetail = (expenseId) => setSelectedExpenseId(expenseId);
  const closeDetail = () => setSelectedExpenseId(null);
  const goToPage = (pageNumber) => setPage(pageNumber - 1);

  // 지출 추가 성공 콜백
  const handleAddSuccess = (newExpense, balancesObj) => {
    setExpenses(prev => [newExpense, ...prev]);
    setBalances(balancesObj);
    setNotification({ message: '지출이 성공적으로 등록되었습니다.', type: 'register' });
    setShowAddDialog(false);
  };

  // 지출 수정 성공 콜백
  const handleUpdateSuccess = (updatedExpense, balancesObj) => {
    setExpenses(prev =>
      prev.map(exp =>
        exp.id === updatedExpense.id
          ? {
              ...exp,
              description: updatedExpense.description,
              amount: updatedExpense.amount,
              category: updatedExpense.category,
              createdAt: updatedExpense.createdAt
            }
          : exp
      )
    );
    setBalances(balancesObj);
    setNotification({ message: '지출이 성공적으로 수정되었습니다.', type: 'update' });
    closeDetail();
  };

  // 지출 삭제 성공 콜백
  const handleDeleteSuccess = (deletedId, balancesObj) => {
    setExpenses(prev => prev.filter(exp => exp.id !== deletedId));
    setBalances(balancesObj);
    setNotification({ message: '지출이 성공적으로 삭제되었습니다.', type: 'delete' });
    closeDetail();
  };

  // 숫자 또는 대체 문자열 반환 헬퍼
  const fmt = (value) =>
    value != null ? value.toLocaleString() : '-';

  // 날짜 포맷팅 (YYYY-MM-DD)
  const formatDate = (dateString) => {
    const date = new Date(dateString);
    return date.toLocaleDateString('ko-KR', { 
      year: 'numeric', 
      month: 'long', 
      day: 'numeric',
      weekday: 'short'
    });
  };

  return (
    <div className="expense-tracker">
      <div className="header">
        <h1 className="title">여행 경비 매니저</h1>
        <div className="header-actions">
          {/* 여기에 추가 액션 버튼들 */}
        </div>
      </div>

      <div className="content">
        <h2 className="section-title">지출 내역</h2>
        
        {/* 서버에서 받은 최신 잔고 및 알림 배너 */}
        {(balances || notification.message) && (
          <div className="balance-banner">
            {balances && (
              <div>
                <span className="label">원화 잔고:</span>
                <strong>₩{fmt(balances.balance)}</strong>
                &nbsp;&nbsp;&nbsp;&nbsp;
                <span className="label">외화 잔고:</span>
                <strong>${fmt(balances.foreignBalance)}</strong>
              </div>
            )}
            {notification.message && (
              <div className={`notification ${notification.type}`}>
                {notification.message}
              </div>
            )}
          </div>
        )}
        
        <div className="actions">
          <button className="btn btn-outlined">
            <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
              <path d="M12 20V10"></path>
              <path d="M18 14l-6-6-6 6"></path>
              <line x1="6" y1="4" x2="18" y2="4"></line>
            </svg>
            예산 수정
          </button>
          <button className="btn btn-filled" onClick={() => setShowAddDialog(true)}>
            <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
              <circle cx="12" cy="12" r="10"></circle>
              <line x1="12" y1="8" x2="12" y2="16"></line>
              <line x1="8" y1="12" x2="16" y2="12"></line>
            </svg>
            지출 추가
          </button>
        </div>

        {expenses.length === 0 ? (
          <div className="empty-state">
            <h3>지출 내역이 없습니다</h3>
            <p>'지출 추가' 버튼을 클릭하여 첫 지출을 등록해보세요.</p>
          </div>
        ) : (
          <div className="expense-table">
            <table>
              <thead>
                <tr>
                  <th>제목</th>
                  <th>가격 (KRW)</th>
                  <th>카테고리</th>
                  <th>날짜</th>
                  <th>결제자</th>
                </tr>
              </thead>
              <tbody>
                {expenses.map(exp => (
                  <tr
                    key={exp.id}
                    onClick={() => openDetail(exp.id)}
                    style={{ cursor: 'pointer' }}
                  >
                    <td>{exp.description}</td>
                    <td className="amount">₩{exp.amount.toLocaleString()}</td>
                    <td>
                      <span 
                        className="category" 
                        data-category={exp.category}
                      >
                        {CATEGORY_LABELS[exp.category] || exp.category}
                      </span>
                    </td>
                    <td>{formatDate(exp.createdAt)}</td>
                    <td>{exp.payerNickname}</td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}

        {/* 페이지 네비게이션 */}
        {expenses.length > 0 && totalPages > 1 && (
          <div className="pagination">
            <button 
              onClick={() => goToPage(page)} 
              disabled={page === 0}
            >
              <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
                <polyline points="15 18 9 12 15 6"></polyline>
              </svg>
            </button>
            
            {Array.from({ length: totalPages }, (_, i) => {
              // 현재 페이지 주변의 페이지 버튼만 표시
              const pageNum = i + 1;
              const currentPage = page + 1;
              
              // 첫 페이지, 마지막 페이지, 현재 페이지 주변의 페이지만 표시
              if (
                pageNum === 1 || 
                pageNum === totalPages || 
                (pageNum >= currentPage - 1 && pageNum <= currentPage + 1)
              ) {
                return (
                  <button
                    key={i}
                    className={pageNum === currentPage ? 'active' : ''}
                    onClick={() => goToPage(pageNum)}
                  >
                    {pageNum}
                  </button>
                );
              }
              
              // 생략 부호 표시 (현재 페이지의 왼쪽)
              if (pageNum === currentPage - 2 && currentPage > 3) {
                return <span key={`ellipsis-left`} className="pagination-ellipsis">...</span>;
              }
              
              // 생략 부호 표시 (현재 페이지의 오른쪽)
              if (pageNum === currentPage + 2 && currentPage < totalPages - 2) {
                return <span key={`ellipsis-right`} className="pagination-ellipsis">...</span>;
              }
              
              return null;
            })}
            
            <button
              onClick={() => goToPage(page + 2)}
              disabled={page + 1 === totalPages}
            >
              <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
                <polyline points="9 18 15 12 9 6"></polyline>
              </svg>
            </button>
          </div>
        )}

        {showAddDialog && (
          <AddExpenseDialog
            onClose={() => setShowAddDialog(false)}
            onSuccess={handleAddSuccess}
          />
        )}

        {selectedExpenseId && (
          <ExpenseDetailDialog
            expenseId={selectedExpenseId}
            onClose={closeDetail}
            onUpdate={handleUpdateSuccess}
            onDelete={handleDeleteSuccess}
          />
        )}
      </div>
    </div>
  );
}