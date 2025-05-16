export function UserProfile({ user, label }) {
  return (
    <div>
      <p className="text-sm text-muted">{label}</p>
      <div className="flex items-center mt-1 space-x-2">
        {user?.avatar ? (
          <img src={user.avatar || "/placeholder.svg"} alt={user.name} className="w-8 h-8 rounded-full" />
        ) : (
          <div className="w-8 h-8 rounded-full bg-gray-200 flex items-center justify-center">
            {user?.name.charAt(0) || "?"}
          </div>
        )}
        <span className="font-medium">{user?.name || "알 수 없음"}</span>
      </div>
    </div>
  )
}
