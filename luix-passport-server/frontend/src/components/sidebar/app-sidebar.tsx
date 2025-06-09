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
  IconVocabulary
} from "@tabler/icons-react"
import { loginUserStore } from "@/stores/login-user-store"
import { useStore } from "exome/react"
import { appInfoStore } from "@/stores/app-info-store"

export function AppSidebar({ ...props }: React.ComponentProps<typeof Sidebar>) {
  const { appInfo } = useStore(appInfoStore)
  const { loginUser } = useStore(loginUserStore)
  const navGroups  = [
    {
      title: "General",
      items: [
        {
          title: "Console",
          url: "/console",
          icon: IconDashboard,
        },
        ...(loginUser.isOnlyUser ? [
          {
            title: "Account",
            url: "/account",
            icon: IconUser,
          },
          {
            title: "Notifications",
            url: "/notifications",
            icon: IconBellRinging,
          }
        ] : []),
        ...(loginUser.isAdmin ? [
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
                title: "Permissions",
                url: "/permissions",
                icon: IconParkingCircle,
              },
              {
                title: "Roles",
                url: "/roles",
                icon: IconUserCircle,
              },
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
    ...(loginUser.isDeveloper ? [
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
        <AppSidebarHeader/>
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
