import { useEffect, useState } from "react";
import { useParams } from "react-router-dom";
import SetBudgetDialog from "./components/set-budget-dialog";
import AddBudgetDialog from "./components/add-budget-dialog";
import EditBudgetDialog from "./components/edit-budget-dialog";

export function BudgetPage() {
  const { teamId } = useParams();
  const [dialogType, setDialogType] = useState(null); // 'set' | 'edit' | 'add' | null
  const [budget, setBudget] = useState(null);
  const [loading, setLoading] = useState(true);

  const token = localStorage.getItem("accessToken");

  const handleClose = () => setDialogType(null);

  const handleUpdate = async (updatePayload) => {
    try {
      const res = await fetch(`/api/teams/${teamId}/budget`, {
        method: "PATCH",
        headers: {
          "Content-Type": "application/json",
          "Authorization": `Bearer ${token}`,
        },
        body: JSON.stringify(updatePayload),
      });

      if (!res.ok) {
        if (res.status === 404) {
          alert("예산 정보를 찾을 수 없습니다.");
        } else {
          alert("예산 업데이트에 실패했습니다.");
        }
        return;
      }

      const updatedBudget = await res.json();
      setBudget(updatedBudget);
      setDialogType(null);
    } catch (err) {
      alert("서버와 통신 중 오류가 발생했습니다.");
      console.error(err);
    }
  };

  const handleDelete = async () => {
    if (!window.confirm("정말로 예산을 삭제하시겠습니까?")) return;

    try {
      const res = await fetch(`/api/teams/${teamId}/budget`, {
        method: "DELETE",
        headers: {
          "Authorization": `Bearer ${token}`,
        },
      });

      if (res.status === 204) {
        alert("예산이 성공적으로 삭제되었습니다.");
        setBudget(null);
        setDialogType(null);
      } else if (res.status === 404) {
        alert("예산 정보를 찾을 수 없습니다.");
      } else {
        alert("예산 삭제에 실패했습니다.");
      }
    } catch (err) {
      alert("서버와 통신 중 오류가 발생했습니다.");
      console.error(err);
    }
  };

  useEffect(() => {
    const fetchBudget = async () => {
      try {
        const res = await fetch(`/api/teams/${teamId}/budget`, {
          headers: {
            "Authorization": `Bearer ${token}`,
          },
        });

        if (!res.ok) throw new Error("서버 응답 오류");

        const data = await res.json();
        setBudget(data);
      } catch (err) {
        console.error("예산 정보를 불러오는 데 실패했습니다:", err);
      } finally {
        setLoading(false);
      }
    };

    fetchBudget();
  }, [teamId, token]);

  if (loading) return <p className="text-center mt-10 text-gray-600">불러오는 중...</p>;
  if (!budget) return <p className="text-center mt-10 text-red-500">예산 정보를 찾을 수 없습니다.</p>;

  return (
    <div className="max-w-2xl mx-auto mt-10 p-6 bg-white shadow-md rounded-lg space-y-6">
      <h1 className="text-2xl font-bold text-gray-800">
        [{budget?.team?.name || `팀 ${teamId}`}] 예산
      </h1>

      <div className="space-y-2 text-gray-700">
        <p>총 예산: <span className="font-medium">{budget?.totalAmount?.toLocaleString() || 0} KRW</span></p>
        <p>원화 잔고: <span className="font-medium">{budget?.balance?.toLocaleString() || 0} KRW</span></p>
        <p>외화 잔고: <span className="font-medium">{budget?.foreignBalance?.toLocaleString() || 0} {budget?.foreignCurrency || 'KRW'}</span></p>
        <p>평균 환율: <span className="font-medium">{budget?.avgExchangeRate || 0}</span></p>
      </div>

      <div className="flex flex-wrap gap-3 mt-4">
        <button
          className="bg-blue-600 text-white px-4 py-2 rounded-lg hover:bg-blue-700"
          onClick={() => setDialogType("set")}
        >
          예산 설정
        </button>
        <button
          className="bg-yellow-500 text-white px-4 py-2 rounded-lg hover:bg-yellow-600"
          onClick={() => setDialogType("edit")}
        >
          예산 수정
        </button>
        <button
          className="bg-green-600 text-white px-4 py-2 rounded-lg hover:bg-green-700"
          onClick={() => setDialogType("add")}
        >
          예산 추가
        </button>
        <button
          className="bg-red-600 text-white px-4 py-2 rounded-lg hover:bg-red-700 ml-auto"
          onClick={handleDelete}
        >
          예산 삭제
        </button>
      </div>

      {/* Dialogs */}
      {dialogType === "set" && (
        <SetBudgetDialog
          onSubmit={(payload) => {
            setBudget({
              ...budget,
              totalAmount: payload.balance,
              balance: payload.balance,
              foreignCurrency: payload.foreignCurrency,
              avgExchangeRate: payload.avgExchangeRate,
              foreignBalance: payload.foreignBalance,
            });
            setDialogType(null);
          }}
          onClose={handleClose}
        />
      )}

      {dialogType === "edit" && (
        <EditBudgetDialog
          budget={budget}
          onSubmit={handleUpdate}
          onClose={handleClose}
        />
      )}

      {dialogType === "add" && (
        <AddBudgetDialog
          budget={budget}
          onSubmit={handleUpdate}
          onClose={handleClose}
        />
      )}
    </div>
  );
}