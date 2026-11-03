
import { NavLink, useNavigate } from "react-router-dom";
import { logout } from "../services/authService";

/* ---------- Icône SVG ---------- */

const Icon = ({ d }) => (
  <svg
    className="h-5 w-5 shrink-0"
    fill="none"
    viewBox="0 0 24 24"
    stroke="currentColor"
    strokeWidth="1.8"
    aria-hidden="true"
  >
    <path
      strokeLinecap="round"
      strokeLinejoin="round"
      d={d}
    />
  </svg>
);

/* ---------- Icônes Admin ---------- */

const icons = {
  dashboard:
    "M3 12l9-9 9 9M5 10v10a1 1 0 001 1h4v-6h4v6h4a1 1 0 001-1V10",

  users:
    "M16 21v-2a4 4 0 00-4-4H8a4 4 0 00-4 4v2m6-10a4 4 0 100-8 4 4 0 000 8zm6-3a4 4 0 010 8m2 3h4v-2a4 4 0 00-3-3.87",
  
  transactions:
    "M3 10h18M7 15h2m4 0h4M5 5h14a2 2 0 012 2v10a2 2 0 01-2 2H5a2 2 0 01-2-2V7a2 2 0 012-2z",
 deposit:
  "M12 3v12m-5-5l5 5 5-5M5 19h14",
  fraud:
    "M12 22s8-4 8-11V5l-8-3-8 3v6c0 7 8 11 8 11zm-3-11l2 2 4-4",


  logout:
    "M17 16l4-4m0 0l-4-4m4 4H7m6 4v1a3 3 0 01-3 3H6a3 3 0 01-3-3V7a3 3 0 013-3h4a3 3 0 013 3v1",
};

/* ---------- Navigation ---------- */

const menuItems = [
  {
    to: "/admin/dashboard",
    label: "Dashboard",
    icon: icons.dashboard,
  },
  {
    to: "/admin/users",
    label: "Gestion des utilisateurs",
    icon: icons.users,
  },
  {
    to: "/admin/transactions",
    label: "Transactions",
    icon: icons.transactions,
  },
  {
  to: "/admin/deposit",
  label: "Dépôt d'argent",
  icon: icons.deposit,
},
  {
    to: "/admin/fraud-alerts",
    label: "Alertes de fraude",
    icon: icons.fraud,
  },
];

/* ---------- Styles ---------- */

const linkClasses = ({ isActive }) =>
  [
    "group relative flex w-full items-center gap-3",
    "rounded-xl px-4 py-2.5",
    "text-sm font-medium",
    "transition-all duration-200",
    "focus:outline-none focus-visible:ring-4",
    "focus-visible:ring-blue-100",
    isActive
      ? "bg-blue-50 text-blue-600"
      : "text-slate-600 hover:bg-slate-50 hover:text-slate-900",
  ].join(" ");

/* ---------- Admin Sidebar ---------- */

function AdminSidebar() {
  const navigate = useNavigate();

  const handleLogout = () => {
    logout();
    navigate("/login");
  };

  return (
    <aside
      className="
        sticky top-0 hidden h-screen w-64 shrink-0
        flex-col border-r border-slate-200 bg-white
        lg:flex
      "
    >
      {/* Logo */}

      <div
        className="
          flex h-16 shrink-0 items-center gap-2.5
          border-b border-slate-200 px-6
        "
      >
        <div
          className="
            flex h-9 w-9 items-center justify-center
            rounded-xl bg-blue-600
            text-sm font-bold text-white
            shadow-sm shadow-blue-600/20
          "
        >
          P
        </div>

        <div className="flex items-center gap-2">
          <div className="h-2.5 w-2.5 rounded-full bg-blue-600" />
          <span className="text-xl font-bold tracking-tight text-slate-900">
            PayFlow
          </span>
        </div>
      </div>

      {/* Navigation */}

      <nav
        className="flex-1 overflow-y-auto px-4 py-4"
        aria-label="Navigation administration"
      >
        <p className="mb-3 mt-4 px-4 text-[11px] font-semibold uppercase tracking-wider text-slate-400">
          Administration
        </p>

        <div className="space-y-1">
          {menuItems.map((item) => (
            <NavLink
              key={item.to}
              to={item.to}
              end={item.to === "/admin/dashboard"}
              className={linkClasses}
            >
              {({ isActive }) => (
                <>
                  {isActive && (
                    <span
                      className="
                        absolute left-0 top-1/2 h-6 w-1
                        -translate-y-1/2 rounded-r-full bg-blue-600
                      "
                    />
                  )}

                  <Icon d={item.icon} />
                  <span>{item.label}</span>
                </>
              )}
            </NavLink>
          ))}
        </div>
      </nav>

      {/* Déconnexion */}

      <div className="shrink-0 border-t border-slate-200 p-4">
        <button
          type="button"
          onClick={handleLogout}
          className="
            flex w-full items-center gap-3
            rounded-xl px-4 py-2.5
            text-sm font-medium text-red-600
            transition-all duration-200
            hover:bg-red-50
            focus:outline-none
            focus-visible:ring-4
            focus-visible:ring-red-100
          "
        >
          <Icon d={icons.logout} />
          <span>Déconnexion</span>
        </button>
      </div>
    </aside>
  );
}

export default AdminSidebar;
