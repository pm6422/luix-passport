import {
  Sidebar,
  SidebarContent,
  SidebarFooter,
  SidebarHeader,
  SidebarRail,
} from "@/components/ui/sidebar"
import { AppSidebarHeader } from "./app-sidebar-header"
import { AppSidebarNavGroup } from "./app-sidebar-nav-group"
// import { UserNav } from "./user-nav"
import { TeamSwitcher } from "./team-switcher"
import {
  IconDashboard,
  IconUsers,
  IconUserPlus,
  IconUserCircle,
  IconParkingCircle,
  IconShield,
  IconDeviceImac,
  IconBook,
  IconUser,
  IconBellRinging,
  IconApi,
  IconDatabase,
  IconVocabulary,
  IconCalendarStats,
  IconHeartbeat,
  IconTicket,
  IconCrown
} from "@tabler/icons-react"
import { useLoginUserStore } from "@/stores/login-user-store"
import { useAppInfoStore } from "@/stores/app-info-store"
import { ROLE_USER, ROLE_ADMIN, ROLE_DEVELOPER } from "@/domains/role"

export function AppSidebar({ ...props }: React.ComponentProps<typeof Sidebar>) {
  const { appInfo } = useAppInfoStore()
  const { hasRole } = useLoginUserStore()
  const navGroups = [
    {
      title: "General",
      items: [
        {
          title: "Console",
          url: "/console",
          icon: IconDashboard,
        },
        ...((hasRole(ROLE_USER)
          && !hasRole(ROLE_ADMIN)
          && !hasRole(ROLE_DEVELOPER)) ? [
          {
            title: "Account",
            url: "/account",
            icon: IconUser,
          },
          {
            title: "Membership",
            url: "/account/membership",
            icon: IconCrown,
          },
          {
            title: "Inbox",
            url: "/inbox",
            icon: IconBellRinging,
          }
        ] : []),
        ...(hasRole(ROLE_ADMIN) ? [
          {
            title: "User",
            icon: IconUsers,
            items: [
              {
                title: "Users",
                url: "/users",
                icon: IconUserPlus,
              },
              {
                title: "Roles",
                url: "/roles",
                icon: IconUserCircle,
              },
              {
                title: "Permissions",
                url: "/permissions",
                icon: IconParkingCircle,
              },
              {
                title: "Promo Codes",
                url: "/promo-codes",
                icon: IconTicket,
              }
            ],
          },
          {
            title: "Authentication",
            icon: IconShield,
            items: [
              {
                title: "Oauth2 Clients",
                url: "/oauth-clients",
                icon: IconDeviceImac,
              }
            ],
          }
        ] : []),
      ],
    },
    ...(hasRole(ROLE_DEVELOPER) ? [
      {
        title: "Developer Tools",
        items: [
          ...(appInfo.apiDocsEnabled ? [
            {
              title: "Data Dictionaries",
              url: "/data-dicts",
              icon: IconVocabulary,
            }
          ] : []),
          {
            title: "Health Check",
            url: "/health-check",
            icon: IconHeartbeat,
          },
          {
            title: "Schedule Logs",
            url: "/schedule-logs",
            icon: IconCalendarStats,
          },
          {
            title: "API Documentation",
            url: "/api-docs",
            icon: IconApi,
          },
          {
            title: "DB Schema",
            url: "/db-schema",
            icon: IconDatabase,
          }
        ],
      }
    ] : []),
    {
      title: "Other",
      items: [
        {
          title: "Technical Docs",
          url: "/tech-docs",
          icon: IconBook,
        },
      ],
    },
  ]

  return (
    <Sidebar collapsible="icon" variant="floating" {...props}>
      <SidebarHeader>
        <AppSidebarHeader />
      </SidebarHeader>
      <SidebarContent>
        {navGroups.map((props) => (
          <AppSidebarNavGroup key={props.title} {...props} />
        ))}
      </SidebarContent>
      <SidebarFooter>
        {/*<UserNav/>*/}
        <TeamSwitcher />
      </SidebarFooter>
      <SidebarRail />
    </Sidebar>
  )
}
