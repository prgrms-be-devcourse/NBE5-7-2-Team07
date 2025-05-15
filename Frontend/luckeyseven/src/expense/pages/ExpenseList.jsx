import React, { useState, useEffect } from 'react';
import AddExpenseDialog from './AddExpenseDialog';
import ExpenseDetailDialog from './ExpenseDetailDialog';
import '../styles/expenseList.css';
import { getListExpense } from '../service/ExpenseService';

const CATEGORY_LABELS = {
  MEAL: '식사',
  SNACK: '간식',
  TRANSPORT: '교통',
  ACCOMMODATION: '숙박',
  MISCELLANEOUS: '기타',
};

export default function ExpenseList({ teamId = 1 }) {
  const [showAddDialog, setShowAddDialog] = useState(false);
  const [selectedExpense, setSelectedExpense] = useState(null);

  // 페이징 상태
  const [expenses, setExpenses] = useState([]);
  const [page, setPage] = useState(0); // 0-based index
  const [size] = useState(10);
  const [totalPages, setTotalPages] = useState(1);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  // 데이터 불러오기
  useEffect(() => {
    const fetchExpenses = async () => {
      setLoading(true);
      try {
        const data = await getListExpense(teamId, page, size);
        setExpenses(data.content);
        setTotalPages(data.totalPages);
      } catch (err) {
        setError(err);
      } finally {
        setLoading(false);
      }
    };
    fetchExpenses();
  }, [teamId, page, size]);

  if (loading) return <div className="loading">로딩 중…</div>;
  if (error)   return <div className="error">에러 발생: {error.message}</div>;

  const openDetail = (expense) => setSelectedExpense(expense);
  const closeDetail = () => setSelectedExpense(null);
  const goToPage = (pageNumber) => setPage(pageNumber - 1);

  return (
    <div className="expense-tracker">
      <div className="header">
        <h1 className="title">여행 경비 매니저</h1>
      </div>

      <div className="content">
        <h2 className="section-title">지출 내역</h2>
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
              {expenses.map((exp) => (
                <tr key={exp.id} onClick={() => openDetail(exp)} style={{ cursor: 'pointer' }}>
                  <td>{exp.description}</td>
                  <td className="amount">₩{exp.amount.toLocaleString()}</td>
                  <td className="category">{CATEGORY_LABELS[exp.category] || exp.category}</td>
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
              key={i + 1}
              className={i === page ? 'active' : ''}
              onClick={() => goToPage(i + 1)}
            >
              {i + 1}
            </button>
          ))}
          <button onClick={() => goToPage(page + 2)} disabled={page + 1 === totalPages}>
            다음
          </button>
        </div>

        {showAddDialog && <AddExpenseDialog onClose={() => setShowAddDialog(false)} />}
        {selectedExpense && (
          <ExpenseDetailDialog
            expense={selectedExpense}
            onClose={closeDetail}
            onUpdate={(updated) => console.log('업데이트된 지출:', updated)}
            onDelete={(id) => {
              console.log('삭제할 지출 ID:', id);
              closeDetail();
            }}
          />
        )}
      </div>
    </div>
  );
}
