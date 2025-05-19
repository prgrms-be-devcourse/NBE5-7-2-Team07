import React, { useState } from "react";
import { useParams } from "react-router-dom";

export default function SetBudgetDialog({ onSubmit, onClose }) {
  const { teamId } = useParams();

  const [totalAmount, setTotalAmount] = useState("");
  const [foreignCurrency, setForeignCurrency] = useState("KRW");
  const [isExchanged, setIsExchanged] = useState(false);
  const [exchangeRate, setExchangeRate] = useState("");

  const handleSubmit = async () => {
    const payload = {
      totalAmount: parseFloat(totalAmount || "0"),
      isExchanged,
      exchangeRate: isExchanged ? parseFloat(exchangeRate || "0") : null,
      foreignCurrency,
    };

    try {
      const res = await fetch(`/api/teams/${teamId}/budget`, {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify(payload),
      });

      if (!res.ok) {
        if (res.status === 409) {
          alert("이미 예산이 설정되어 있습니다.");
        } else {
          alert("예산 설정 중 오류가 발생했습니다.");
        }
        return;
      }

      const data = await res.json();
      onSubmit(data);
    } catch (error) {
      console.error("예산 생성 실패:", error);
      alert("서버 연결에 실패했습니다.");
    }
  };

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center bg-black bg-opacity-50">
      <div className="w-full max-w-md p-6 bg-white rounded-2xl shadow-lg">
        <h2 className="text-2xl font-bold mb-6 text-center text-gray-800">예산 설정</h2>

        <div className="space-y-4">
          {/* 총 예산 */}
          <div>
            <label className="block text-sm font-medium text-gray-700">총 예산</label>
            <input
              type="number"
              className="w-full border border-gray-300 rounded-lg px-4 py-2 mt-1 focus:outline-none focus:ring-2 focus:ring-blue-500"
              placeholder="예: 100000"
              step="100"
              value={totalAmount}
              onChange={(e) => setTotalAmount(e.target.value)}
            />
          </div>

          {/* 통화 선택 */}
          <div>
            <label className="block text-sm font-medium text-gray-700">통화</label>
            <select
              className="w-full border border-gray-300 rounded-lg px-4 py-2 mt-1 focus:outline-none focus:ring-2 focus:ring-blue-500"
              value={foreignCurrency}
              onChange={(e) => setForeignCurrency(e.target.value)}
            >
              <option value="USD">USD - 미국 달러</option>
              <option value="EUR">EUR - 유로</option>
              <option value="KRW">KRW - 대한민국 원</option>
              <option value="JPY">JPY - 일본 엔화</option>
              <option value="CNY">CNY - 중국 위안</option>
              <option value="GBP">GBP - 영국 파운드</option>
              <option value="AUD">AUD - 호주 달러</option>
              <option value="CAD">CAD - 캐나다 달러</option>
              <option value="CHF">CHF - 스위스 프랑</option>
              <option value="INR">INR - 인도 루피</option>
              <option value="SGD">SGD - 싱가포르 달러</option>
              <option value="THB">THB - 태국 바트</option>
              <option value="HKD">HKD - 홍콩 달러</option>
              <option value="RUB">RUB - 러시아 루블</option>
              <option value="BRL">BRL - 브라질 헤알</option>
            </select>
          </div>

          {/* 환전 여부 */}
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">환전 여부</label>
            <div className="flex gap-4">
              <button
                className={`flex-1 py-2 rounded-lg border font-medium transition ${
                  isExchanged
                    ? "bg-blue-600 text-white border-blue-600"
                    : "bg-white text-blue-600 border-blue-600"
                }`}
                onClick={() => setIsExchanged(true)}
                type="button"
              >
                O
              </button>
              <button
                className={`flex-1 py-2 rounded-lg border font-medium transition ${
                  !isExchanged
                    ? "bg-blue-600 text-white border-blue-600"
                    : "bg-white text-blue-600 border-blue-600"
                }`}
                onClick={() => setIsExchanged(false)}
                type="button"
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
                className="w-full border border-gray-300 rounded-lg px-4 py-2 mt-1 focus:outline-none focus:ring-2 focus:ring-blue-500"
                placeholder="예: 1393.7"
                value={exchangeRate}
                onChange={(e) => setExchangeRate(e.target.value)}
              />
            </div>
          )}
        </div>

        <div className="flex justify-end gap-2 mt-6">
          <button
            type="button"
            onClick={onClose}
            className="px-4 py-2 rounded-lg border border-gray-300 text-gray-700 hover:bg-gray-100"
          >
            취소
          </button>
          <button
            type="button"
            onClick={handleSubmit}
            className="px-4 py-2 rounded-lg bg-blue-600 text-white hover:bg-blue-700"
          >
            저장
          </button>
        </div>
      </div>
    </div>
  );
}
