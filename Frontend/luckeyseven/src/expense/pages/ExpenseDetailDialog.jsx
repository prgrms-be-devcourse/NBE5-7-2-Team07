import React, { useState } from 'react';
import '../styles/expenseDetailDialog.css';


const CATEGORY_LABELS = {
  MEAL: '식사',
  SNACK: '간식',
  TRANSPORT: '교통',
  ACCOMMODATION: '숙박',
  MISCELLANEOUS: '기타'
};

export default function ExpenseDetailDialog({ expense, onClose, onUpdate, onDelete }) {
  const [isEditing, setIsEditing] = useState(false);
  const [formData, setFormData] = useState({
    title: expense?.title || '',
    amount: expense?.amount || 0,
    category: expense?.category || ''
  });

  if (!expense) return null;

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData(prev => ({ 
      ...prev, 
      [name]: name === 'amount' ? Number(value) : value 
    }));
  };

  const handleSave = () => {
    onUpdate({ ...expense, ...formData });
    setIsEditing(false);
  };

  const handleCancel = () => {
    setFormData({
      title: expense.title,
      amount: expense.amount,
      category: expense.category
    });
    setIsEditing(false);
  };

  return (
    <div className="modal-overlay">
      <div className="modal">
        <header>
          <h3>지출 상세</h3>
          <button className="close-btn" onClick={onClose}>×</button>
        </header>

        <div className="detail-content">
          {isEditing ? (
            <>
              <div className="field">
                <label>제목</label>
                <input
                  type="text"
                  name="title"
                  value={formData.title}
                  onChange={handleChange}
                />
              </div>
              <div className="field">
                <label>금액 (₩)</label>
                <input
                  type="number"
                  name="amount"
                  value={formData.amount}
                  onChange={handleChange}
                />
              </div>
              <div className="field">
                <label>카테고리</label>
                <select
                  name="category"
                  value={formData.category}
                  onChange={handleChange}
                >
                  <option value="" disabled>카테고리를 선택하세요</option>
                  {Object.entries(CATEGORY_LABELS).map(([key, label]) => (
                    <option key={key} value={key}>
                      {label}
                    </option>
                  ))}
                </select>
              </div>
            </>
          ) : (
            <>
              <p><strong>제목:</strong> {expense.title}</p>
              <p><strong>금액:</strong> ₩{expense.amount.toLocaleString()}</p>
              <p><strong>카테고리:</strong> {CATEGORY_LABELS[expense.category]}</p>
              <p><strong>날짜:</strong> {expense.date}</p>
              <p><strong>결제자:</strong> {expense.payer}</p>
            </>
          )}
        </div>

        <div className="modal-actions">
          {isEditing ? (
            <>
              <button onClick={handleCancel}>취소</button>
              <button className="save-btn" onClick={handleSave}>저장</button>
            </>
          ) : (
            <>
              <button onClick={() => setIsEditing(true)}>수정</button>
              <button onClick={() => onDelete(expense.id)}>삭제</button>
            </>
          )}
        </div>
      </div>
    </div>
  );
}