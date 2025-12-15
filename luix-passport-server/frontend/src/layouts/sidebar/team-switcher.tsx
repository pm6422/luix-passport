import * as React from "react"
import {
  IconUsersGroup,
  IconPlus,
  IconSelector
} from "@tabler/icons-react"
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuItem,
  DropdownMenuLabel,
  DropdownMenuSeparator,
  DropdownMenuTrigger,
} from "@/components/ui/dropdown-menu"
import {
  SidebarMenu,
  SidebarMenuButton,
  SidebarMenuItem,
  useSidebar,
} from "@/components/ui/sidebar"

import { CreateTeamDialog } from "@/views/team/dialog/create-team-dialog"
import { TeamService, type Team } from "@/services/team-service";
import { useNavigate } from "react-router-dom";

export function TeamSwitcher() {
  const { isMobile } = useSidebar()
  const navigate = useNavigate()
  const [createTeamOpen, setCreateTeamOpen] = React.useState(false)
  const [teams, setTeams] = React.useState<Team[]>([])
  const [activeTeam, setActiveTeam] = React.useState<Team | null>(null)

  const fetchTeams = React.useCallback(async () => {
    try {
      const { data } = await TeamService.findUserTeams()
      setTeams(data)
      if (data.length > 0 && !activeTeam) {
        setActiveTeam(data[0])
      }
    } catch (e) {
      console.error(e)
    }
  }, [activeTeam])

  React.useEffect(() => {
    fetchTeams()
  }, [fetchTeams])

  return (
    <SidebarMenu>
      <SidebarMenuItem>
        <DropdownMenu>
          <DropdownMenuTrigger asChild>
            <SidebarMenuButton
              size="lg"
              className="data-[state=open]:bg-sidebar-accent data-[state=open]:text-sidebar-accent-foreground"
            >
              <div className="bg-sidebar-primary text-sidebar-primary-foreground flex aspect-square size-8 items-center justify-center rounded-lg">
                {activeTeam?.photo ? (
                  <img src={`data:image/png;base64,${activeTeam.photo}`} className="size-full rounded-lg object-cover" alt={activeTeam.name} />
                ) : (
                  <IconUsersGroup className="size-4" />
                )}
              </div>
              <div className="grid flex-1 text-start text-sm leading-tight">
                <span className="truncate font-semibold">
                  {activeTeam?.name || "Select Team"}
                </span>
                <span className="truncate text-xs">Personal</span>
              </div>
              <IconSelector className="ms-auto" />
            </SidebarMenuButton>
          </DropdownMenuTrigger>
          <DropdownMenuContent
            className="min-w-48 rounded-lg"
            align="start"
            side={isMobile ? "bottom" : "right"}
            sideOffset={4}
          >
            <DropdownMenuLabel className="text-muted-foreground text-xs">
              Teams
            </DropdownMenuLabel>
            {teams.map((team) => (
              <DropdownMenuItem
                key={team.id}
                onClick={() => {
                  setActiveTeam(team)
                  navigate(`/teams/${team.id}`)
                }}
                className="gap-2 p-2"
              >
                <div className="flex size-6 items-center justify-center rounded-sm border">
                  {team.photo ? (
                    <img src={`data:image/png;base64,${team.photo}`} className="size-full rounded-sm object-cover" alt={team.name} />
                  ) : (
                    <IconUsersGroup className="size-4 shrink-0" />
                  )}
                </div>
                {team.name}
              </DropdownMenuItem>
            ))}
            <DropdownMenuSeparator />
            <DropdownMenuItem className="gap-2 p-2" onClick={() => setCreateTeamOpen(true)}>
              <div className="flex size-6 items-center justify-center">
                <IconPlus className="size-4"/>
              </div>
              <div className="text-muted-foreground font-medium">Create Team</div>
            </DropdownMenuItem>
          </DropdownMenuContent>
        </DropdownMenu>
        <CreateTeamDialog open={createTeamOpen} onOpenChange={setCreateTeamOpen} onSuccess={fetchTeams} />
      </SidebarMenuItem>
    </SidebarMenu >
  )
}
