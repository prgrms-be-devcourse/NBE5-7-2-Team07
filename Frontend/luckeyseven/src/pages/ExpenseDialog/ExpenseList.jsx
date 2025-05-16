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

  if (loading) return <div className="loading">로딩 중…</div>;
  if (error)   return <div className="error">에러 발생: {error.message}</div>;

  const openDetail = (expenseId) => setSelectedExpenseId(expenseId);
  const closeDetail = () => setSelectedExpenseId(null);
  const goToPage  = (pageNumber) => setPage(pageNumber - 1);

  // 지출 추가 성공 콜백
  const handleAddSuccess = (newExpense, balancesObj) => {
    setExpenses(prev => [newExpense, ...prev]);
    setBalances(balancesObj);
    setNotification({ message: '지출이 등록되었습니다.', type: 'register' });
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
    setNotification({ message: '지출이 수정되었습니다.', type: 'update' });
    closeDetail();
  };

  // 지출 삭제 성공 콜백
  const handleDeleteSuccess = (deletedId, balancesObj) => {
    setExpenses(prev => prev.filter(exp => exp.id !== deletedId));
    setBalances(balancesObj);
    setNotification({ message: '지출이 삭제되었습니다.', type: 'delete' });
    closeDetail();
  };

  // 숫자 또는 대체 문자열 반환 헬퍼
  const fmt = (value) =>
    value != null ? value.toLocaleString() : '-';

  return (
    <div className="expense-tracker">
      <div className="header">
        <h1 className="title">여행 경비 매니저</h1>
      </div>

      <div className="content">
        <h2 className="section-title">지출 내역</h2>
        {/* 서버에서 받은 최신 잔고 및 알림 배너 */}
      {(balances || notification.message) && (
        <div className="balance-banner">
          {balances && (
            <>
              <div>
                <span className="label">원화 잔고:</span>
                <strong>₩{fmt(balances.balance)}</strong>
                &nbsp;&nbsp;
                <span className="label">외화 잔고:</span>
                <strong>${fmt(balances.foreignBalance)}</strong>
              </div>
              <br />
            </>
          )}
          {notification.message && (
            <span className={`notification ${notification.type}`}>
              {notification.message}
            </span>
          )}
        </div>
      )}
        <div className="actions" style={{ justifyContent: 'flex-end' }}>
          <button className="btn btn-outlined">예산 수정</button>
          <button className="btn btn-filled" onClick={() => setShowAddDialog(true)}>
            지출 추가
          </button>
        </div>

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
                  <td className="category">
                    {CATEGORY_LABELS[exp.category] || exp.category}
                  </td>
                  <td>{new Date(exp.createdAt).toLocaleDateString()}</td>
                  <td>{exp.payerNickname}</td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>

        {/* 페이지 네비게이션 */}
        <div className="pagination">
          <button onClick={() => goToPage(page)} disabled={page === 0}>
            이전
          </button>
          {Array.from({ length: totalPages }, (_, i) => (
            <button
              key={i}
              className={i === page ? 'active' : ''}
              onClick={() => goToPage(i + 1)}
            >
              {i + 1}
            </button>
          ))}
          <button
            onClick={() => goToPage(page + 2)}
            disabled={page + 1 === totalPages}
          >
            다음
          </button>
        </div>

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
