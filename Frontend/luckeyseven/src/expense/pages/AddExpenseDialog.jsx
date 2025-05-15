import React, { useState } from 'react';
import '../styles/addExpenseDialog.css';


const CATEGORY_LABELS = {
  MEAL: '식사',
  SNACK: '간식',
  TRANSPORT: '교통',
  ACCOMMODATION: '숙박',
  MISCELLANEOUS: '기타',
};
const PAYMENT_LABELS = {
  CARD: '카드',
  CASH: '현금',
  OTHER: '기타',
};


const categories = Object.keys(CATEGORY_LABELS);
const paymentMethods = Object.keys(PAYMENT_LABELS);
const users = [
  { id: 1, name: 'John Doe' },
  { id: 2, name: 'Jane Smith' },
  { id: 3, name: 'Mike Johnson' },
  { id: 4, name: 'Sarah Williams' }
];

export default function AddExpenseDialog({ onClose }) {
  const [form, setForm] = useState({
    description: '',
    amount: '',
    category: categories[0],
    payerId: users[0].id,
    paymentMethod: paymentMethods[0],
    settlerIds: [users[0].id]
  });

  const handleChange = e => {
    const { name, value, options } = e.target;
    if (name === 'settlerIds') {
      const selected = Array.from(options)
        .filter(o => o.selected)
        .map(o => Number(o.value));
      setForm(f => ({ ...f, settlerIds: selected }));
    } else if (name === 'amount' || name === 'payerId') {
      setForm(f => ({ ...f, [name]: Number(value) }));
    } else {
      setForm(f => ({ ...f, [name]: value }));
    }
  };

  const handleSubmit = e => {
    e.preventDefault();
    console.log('새 지출:', form);
    onClose();
  };

  return (
    <div className="modal-overlay">
      <div className="modal">
        <header>
          <h3>새 지출 추가</h3>
          <button className="close-btn" onClick={onClose}>×</button>
        </header>
        <form onSubmit={handleSubmit} className="modal-form">
          <label>
            설명
            <input
              name="description"
              value={form.description}
              onChange={handleChange}
              required
            />
          </label>
          <label>
            금액 (₩)
            <input
              name="amount"
              type="number"
              value={form.amount}
              onChange={handleChange}
              min="0"
              required
            />
          </label>
          <label>
            카테고리
            <select name="category" value={form.category} onChange={handleChange}>
              {categories.map(key => (
                <option key={key} value={key}>
                  {CATEGORY_LABELS[key]}
                </option>
              ))}
            </select>
          </label>
          <label>
            결제자
            <select name="payerId" value={form.payerId} onChange={handleChange}>
              {users.map(u => (
                <option key={u.id} value={u.id}>{u.name}</option>
              ))}
            </select>
          </label>
          <label>
            결제 수단
            <select name="paymentMethod" value={form.paymentMethod} onChange={handleChange}>
              {paymentMethods.map(key => (
                <option key={key} value={key}>
                  {PAYMENT_LABELS[key]}
                </option>
              ))}
            </select>
          </label>
          <label>
            정산 대상자
            <select
              name="settlerIds"
              multiple
              value={form.settlerIds}
              onChange={handleChange}
              required
            >
              {users.map(u => (
                <option key={u.id} value={u.id}>{u.name}</option>
              ))}
            </select>
          </label>
          <div className="modal-actions">
            <button type="button" onClick={onClose}>취소</button>
            <button type="submit">저장</button>
          </div>
        </form>
      </div>
    </div>
  );
}