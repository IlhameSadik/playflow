import { NavLink } from "react-router-dom";

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

/* ---------- Icônes ---------- */

const icons = {
    dashboard:
        "M3 12l9-9 9 9M5 10v10a1 1 0 001 1h4v-6h4v6h4a1 1 0 001-1V10",

    transactions:
        "M3 10h18M7 15h2m4 0h4M5 5h14a2 2 0 012 2v10a2 2 0 01-2 2H5a2 2 0 01-2-2V7a2 2 0 012-2z",

    transfer:
        "M7 7h10m0 0l-4-4m4 4l-4 4M17 17H7m0 0l4 4m-4-4l4-4",

    deposit:
        "M12 4v16m8-8H4",

    withdraw:
        "M20 12H4",

    notifications:
        "M15 17h5l-1.4-1.4A2 2 0 0118 14.2V11a6 6 0 10-12 0v3.2c0 .5-.2 1-.6 1.4L4 17h5m6 0a3 3 0 11-6 0",

    settings:
        "M10.3 4.3a1.7 1.7 0 013.4 0 1.7 1.7 0 002.6 1.1 1.7 1.7 0 012.4 2.4 1.7 1.7 0 001.1 2.6 1.7 1.7 0 010 3.4 1.7 1.7 0 00-1.1 2.6 1.7 1.7 0 01-2.4 2.4 1.7 1.7 0 00-2.6 1.1 1.7 1.7 0 01-3.4 0 1.7 1.7 0 00-2.6-1.1 1.7 1.7 0 01-2.4-2.4 1.7 1.7 0 00-1.1-2.6 1.7 1.7 0 010-3.4 1.7 1.7 0 001.1-2.6 1.7 1.7 0 012.4-2.4 1.7 1.7 0 002.6-1.1zM15 12a3 3 0 11-6 0 3 3 0 016 0z",

    logout:
        "M17 16l4-4m0 0l-4-4m4 4H7m6 4v1a3 3 0 01-3 3H6a3 3 0 01-3-3V7a3 3 0 013-3h4a3 3 0 013 3v1",
};

/* ---------- Navigation ---------- */

const mainNav = [
    {
        to: "/dashboard",
        label: "Dashboard",
        icon: icons.dashboard,
    },
    {
        to: "/transactions",
        label: "Transactions",
        icon: icons.transactions,
    },
];

const actionsNav = [
    {
        to: "/transfer",
        label: "Transférer",
        icon: icons.transfer,
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

/* ---------- Navigation Item ---------- */

const NavItem = ({ to, label, icon, badge }) => (
    <NavLink to={to} className={linkClasses}>
        {({ isActive }) => (
            <>
                {/* Active indicator */}
                {isActive && (
                    <span
                        className="absolute left-0 top-1/2 h-6 w-1
                        -translate-y-1/2 rounded-r-full bg-blue-600"
                    />
                )}

                <Icon d={icon} />

                <span >
                    {label}
                </span>

                {badge > 0 && (
    <span className="flex h-5 min-w-5 items-center justify-center rounded-full bg-red-500 px-1.5 text-[10px] font-bold text-white">
        {badge > 99 ? "99+" : badge}
    </span>
)}
            </>
        )}
    </NavLink>
);

/* ---------- Section title ---------- */

const SectionTitle = ({ children }) => (
    <p className="mb-2 mt-6 px-4 text-[11px] font-semibold uppercase tracking-wider text-slate-400">
        {children}
    </p>
);

/* ---------- Sidebar ---------- */

function Sidebar({
    onLogout,
    notificationsCount = 0,
}) {
    return (
        <aside
            className="
                sticky top-0 hidden h-screen w-64 shrink-0
                flex-col border-r border-slate-200 bg-white
                lg:flex
            "
        >

            {/* ---------- Logo ---------- */}

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

            {/* ---------- Navigation ---------- */}

            <nav
                className="flex-1 overflow-y-auto px-4 p-4 mb-3 mt-6"
                aria-label="Navigation principale"
            >
              

                <div className="space-y-1">
                    {mainNav.map((item) => (
                        <NavItem
                            key={item.to}
                            {...item}
                        />
                    ))}

                    <NavItem
                        to="/notifications"
                        label="Notifications"
                        icon={icons.notifications}
                        badge={notificationsCount}
                    />
                </div>

                {/* Actions */}
               

                <div className="space-y-1">
                    {actionsNav.map((item) => (
                        <NavItem
                            key={item.to}
                            {...item}
                        />
                    ))}
                </div>

            </nav>

            {/* ---------- Bottom ---------- */}

            <div
                className="
                    shrink-0 space-y-1
                    border-t border-slate-200
                    p-4
                "
            >

              
                 
                {/* Déconnexion */}
                <button
                    type="button"
                    onClick={onLogout}
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

                    <span>
                        Déconnexion
                    </span>
                </button>

            </div>

        </aside>
    );
}

export default Sidebar;