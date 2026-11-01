import { getCurrentUser } from "../services/authService";

function Header({ title, subtitle = "", notificationsCount = 0 }) {
    const user = getCurrentUser();

    const getInitials = (firstName, lastName) => {
        return `${firstName?.[0] || ""}${lastName?.[0] || ""}`.toUpperCase();
    };

    return (
        <header
            className="
                flex h-[76px] items-center justify-between
                border-b border-slate-200 bg-white
                px-6 lg:px-8
            "
        >
            {/* Left */}
            <div>
                <h1 className="text-lg font-semibold text-slate-900"></h1>
                  
            </div>

            {/* Right */}
            <div className="flex items-center gap-4">

                {/* Notifications */}
                <button
                    type="button"
                    className="
                        relative flex h-10 w-10 items-center justify-center
                        rounded-xl text-slate-500
                        transition-all duration-200
                        hover:bg-slate-100 hover:text-slate-900
                        focus:outline-none
                        focus-visible:ring-4
                        focus-visible:ring-blue-100
                    "
                    aria-label={
                        notificationsCount > 0
                            ? `${notificationsCount} notifications non lues`
                            : "Notifications"
                    }
                >
                    <svg
                        className="h-[19px] w-[19px]"
                        viewBox="0 0 24 24"
                        fill="none"
                        stroke="currentColor"
                        strokeWidth="1.8"
                        strokeLinecap="round"
                        strokeLinejoin="round"
                        aria-hidden="true"
                    >
                        <path d="M18 8a6 6 0 00-12 0c0 7-3 7-3 9h18c0-2-3-2-3-9" />
                        <path d="M10 21h4" />
                    </svg>

                    {notificationsCount > 0 && (
                        <span
                            className="
                                absolute right-1.5 top-1.5
                                h-2 w-2 rounded-full
                                bg-red-500
                                ring-2 ring-white
                            "
                        />
                    )}
                </button>

                {/* Separator */}
                <div className="hidden h-8 w-px bg-slate-200 sm:block" />

                {/* User */}
                <div className="flex items-center gap-3">

                    {/* Avatar */}
                    <div
                        className="
                            flex h-10 w-10 shrink-0
                            items-center justify-center
                            rounded-full bg-slate-900
                            text-xs font-bold text-white
                        "
                    >
                        {getInitials(
                            user?.firstName,
                            user?.lastName
                        )}
                    </div>

                    {/* User information */}
                    <div className="hidden min-w-0 sm:block">
                        <p className="max-w-[160px] truncate text-sm font-semibold text-slate-900">
                            {user?.firstName} {user?.lastName}
                        </p>

                        <p className="mt-0.5 text-xs text-slate-400">
                            Compte personnel
                        </p>
                    </div>

                </div>

            </div>
        </header>
    );
}

export default Header;