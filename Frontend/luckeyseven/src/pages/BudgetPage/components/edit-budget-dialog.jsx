import { useState } from "react";
import { useParams } from "react-router-dom";

export default function EditBudgetDialog({ budget, onSubmit, onClose }) {
  const { teamId } = useParams();

  const [totalAmount, setTotalAmount] = useState(budget.totalAmount);
  const [isExchanged, setIsExchanged] = useState(!!budget.avgExchangeRate);
  const [exchangeRate, setExchangeRate] = useState(budget.avgExchangeRate || "");

  const handleSubmit = async () => {
    const payload = {
      totalAmount: parseFloat(totalAmount),
      isExchanged,
      exchangeRate: isExchanged ? parseFloat(exchangeRate) : null,
    };

    try {
      const res = await fetch(`/api/teams/${teamId}/budget`, {
        method: "PATCH",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify(payload),
      });

      if (!res.ok) {
        if (res.status === 404) {
          alert("예산 정보를 찾을 수 없습니다.");
        } else {
          alert("예산 수정 중 오류가 발생했습니다.");
        }
        return;
      }

      const data = await res.json();
      onSubmit(data);
    } catch (err) {
      console.error("예산 수정 실패:", err);
      alert("서버와의 통신에 실패했습니다.");
    }
  };

  return (
    <div className="fixed top-1/4 left-1/2 transform -translate-x-1/2 bg-white border border-gray-300 rounded-lg shadow-lg p-6 z-50 w-full max-w-md">
      <h2 className="text-xl font-semibold text-gray-800 mb-4">예산 수정</h2>

      <div className="space-y-4">
        {/* 총 예산 입력 */}
        <div>
          <label className="block text-sm font-medium text-gray-700">총 예산</label>
          <input
            type="number"
            step="100"
            value={totalAmount}
            onChange={(e) => setTotalAmount(e.target.value)}
            className="w-full border rounded-lg px-3 py-2 mt-1 focus:outline-none focus:ring-2 focus:ring-blue-500"
          />
        </div>

        {/* 환전 여부 선택 */}
        <div>
          <label className="block text-sm font-medium text-gray-700 mb-1">환전 여부</label>
          <div className="flex gap-4">
            <button
              type="button"
              onClick={() => setIsExchanged(true)}
              className={`flex-1 py-2 rounded-lg border transition ${
                isExchanged
                  ? "bg-blue-600 text-white border-blue-600"
                  : "text-blue-600 border-blue-600 hover:bg-blue-50"
              }`}
            >
              O
            </button>
            <button
              type="button"
              onClick={() => setIsExchanged(false)}
              className={`flex-1 py-2 rounded-lg border transition ${
                !isExchanged
                  ? "bg-blue-600 text-white border-blue-600"
                  : "text-blue-600 border-blue-600 hover:bg-blue-50"
              }`}
            >
              X
            </button>
          </div>
        </div>

        {/* 환율 입력 */}
        {isExchanged && (
          <div>
            <label className="block text-sm font-medium text-gray-700">환율</label>
            <input
              type="number"
              value={exchangeRate}
              onChange={(e) => setExchangeRate(e.target.value)}
              placeholder="예: 1320.5"
              className="w-full border rounded-lg px-3 py-2 mt-1 focus:outline-none focus:ring-2 focus:ring-blue-500"
            />
          </div>
        )}
      </div>

      {/* 버튼 */}
      <div className="flex justify-end gap-2 mt-6">
        <button
          type="button"
          onClick={onClose}
          className="px-4 py-2 rounded-lg border border-gray-300 text-gray-700 hover:bg-gray-100 transition"
        >
          취소
        </button>
        <button
          type="button"
          onClick={handleSubmit}
          className="px-4 py-2 rounded-lg bg-blue-600 text-white hover:bg-blue-700 transition"
        >
          저장
        </button>
      </div>
    </div>
  );
}
