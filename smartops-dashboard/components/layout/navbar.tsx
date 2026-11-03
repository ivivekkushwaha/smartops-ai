"use client";

import { useState } from "react";
import { Bell, Settings, Search, LogOut, User } from "lucide-react";
import Link from "next/link";
import { useAuth } from "@/lib/auth-context";
import { useRouter } from "next/navigation";
// import { useAlerts } from "@/hooks/use-alerts";
import { useServices } from "@/hooks/use-services";

export function Navbar() {
  const [showUserMenu, setShowUserMenu] = useState(false);

  const { user, logout } = useAuth();
  const router = useRouter();
  // const { alerts } = useAlerts();
  const { services } = useServices();

  // const activeAlerts =
  //   alerts?.filter((alert) => alert.status === "ACTIVE").length ?? 0;

  const healthyServices =
    services?.filter((service) => service.status === "UP").length ?? 0;

  const handleLogout = async () => {
    await logout();
    router.replace("/login");
  };

  const getInitials = (name: string) => {
    return name
      .split(" ")
      .map((part) => part[0])
      .join("")
      .toUpperCase()
      .slice(0, 2);
  };

  return (
    <nav
      className="sticky top-0 z-40 w-full border-b bg-opacity-95 backdrop-blur supports-[backdrop-filter]:bg-opacity-60"
      style={{
        borderColor: "var(--border)",
        backgroundColor: "rgba(8, 13, 26, 0.95)",
      }}
    >
      <div className="flex h-13 items-center gap-3 px-4">
        {/* Logo */}
        <Link href="/" className="flex items-center gap-2 shrink-0">
          <div className="w-7 h-7 rounded-lg bg-gradient-to-br from-blue to-purple flex items-center justify-center text-sm font-bold text-white">
            ⚡
          </div>
          <span className="text-sm font-bold text-text1">
            Smart<span className="text-blue2">Ops</span> AI
          </span>
        </Link>

        {/* Search Bar
        <div className="flex-1 max-w-xs flex items-center gap-2 px-3 py-1.5 rounded-lg border bg-white/4 hover:bg-white/6 transition-colors" style={{
          borderColor: 'var(--border)',
        }}>
          <Search className="w-3.5 h-3.5" style={{ color: 'var(--text3)' }} />
          <input
            placeholder="Search services, logs, metrics..."
            value={searchQuery}
            onChange={(e) => setSearchQuery(e.target.value)}
            className="flex-1 bg-transparent border-0 outline-none text-xs placeholder:text-text3 text-text2"
          />
        </div> */}

        {/* Right Actions */}
        <div className="ml-auto flex items-center gap-2">
          <div className="flex items-center gap-2 px-3 py-1 rounded-lg border">
            <span>{healthyServices}</span>
            <span>Services Online</span>
          </div>

          {/* Notification
          <button
            onClick={() => router.push("/alerts")}
            className="relative w-8 h-8 flex items-center justify-center rounded-lg border transition-colors"
            style={{
              backgroundColor: "rgba(255, 255, 255, 0.04)",
              borderColor: "var(--border)",
            }}
            onMouseEnter={(e) => {
              e.currentTarget.style.backgroundColor =
                "rgba(255, 255, 255, 0.08)";
              e.currentTarget.style.borderColor = "var(--border2)";
            }}
            onMouseLeave={(e) => {
              e.currentTarget.style.backgroundColor =
                "rgba(255, 255, 255, 0.04)";
              e.currentTarget.style.borderColor = "var(--border)";
            }}
          >
            <Bell className="w-4 h-4" style={{ color: "var(--text2)" }} />

            {/* {activeAlerts > 0 && (
              <span
                className="absolute -top-1 -right-1 min-w-[16px] h-4 px-1 rounded-full flex items-center justify-center text-[10px] font-semibold text-white"
                style={{
                  backgroundColor: "#ef4444",
                }}
              >
                {activeAlerts > 99 ? "99+" : activeAlerts}
              </span>
            )} */}
          {/* </button> */} 

          {/* <button
            onClick={() => router.push("/settings")}
            className="w-8 h-8 flex items-center justify-center rounded-lg border transition-colors"
            style={{
              backgroundColor: "rgba(255, 255, 255, 0.04)",
              borderColor: "var(--border)",
            }}
            onMouseEnter={(e) => {
              e.currentTarget.style.backgroundColor =
                "rgba(255, 255, 255, 0.08)";
              e.currentTarget.style.borderColor = "var(--border2)";
            }}
            onMouseLeave={(e) => {
              e.currentTarget.style.backgroundColor =
                "rgba(255, 255, 255, 0.04)";
              e.currentTarget.style.borderColor = "var(--border)";
            }}
          >
            <Settings className="w-4 h-4" style={{ color: "var(--text2)" }} />
          </button> */}

          {/* User Menu */}
          <div className="relative">
            <button
              onClick={() => setShowUserMenu(!showUserMenu)}
              className="w-8 h-8 rounded-lg bg-gradient-to-br from-blue to-purple flex items-center justify-center text-xs font-bold text-white font-mono hover:shadow-lg transition-all"
            >
              {user ? getInitials(user.name) : "U"}
            </button>

           {/* Dropdown Menu */}
{showUserMenu && (
  <div
    className="absolute right-0 mt-2 w-48 rounded-lg border shadow-lg overflow-hidden"
    style={{
      backgroundColor: "var(--bg1)",
      borderColor: "var(--border)",
    }}
  >
    {user && (
      <>
        <div
          className="px-4 py-3 border-b"
          style={{
            borderColor: "var(--border)",
          }}
        >
          <p className="text-sm font-semibold text-text1">
            {user.name}
          </p>
          <p className="text-xs text-text3">
            {user.email}
          </p>
        </div>

        <button
          onClick={() => {
            router.push("/profile");
            setShowUserMenu(false);
          }}
          className="w-full px-4 py-2.5 text-sm text-text2 hover:text-text1 hover:bg-bg2 transition-all flex items-center gap-2 border-b"
          style={{
            borderColor: "var(--border)",
          }}
        >
          <User size={16} />
          Profile Settings
        </button>
      </>
    )}

    <button
      onClick={handleLogout}
      className="w-full px-4 py-2.5 text-sm text-red2 hover:bg-red/5 transition-all flex items-center gap-2"
    >
      <LogOut size={16} />
      Sign Out
    </button>
  </div>
)}
          </div>
        </div>
      </div>
    </nav>
  );
}
