import { useState, useEffect } from "react"
import { Button } from "@/components/ui/button"
import { DateTime } from "@/components/custom/date-time"
import { InviteMembersDialog } from "./invite-dialog"
import { LayoutBody } from "@/layouts/layout-definitions"
import { IconPlus } from "@tabler/icons-react"
import { useParams } from "react-router-dom"
import { Input } from "@/components/ui/input"
import {
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableHeader,
  TableRow,
} from "@/components/ui/table"
import { Avatar, AvatarFallback, AvatarImage } from "@/components/ui/avatar"
import { Badge } from "@/components/ui/badge"
import { TeamService, TeamMember, TeamInvitation } from "@/services/team-service"
import { loginUserStore } from "@/stores/login-user-store"
import { useStore } from "exome/react"

export default function TeamMembers() {
  const [openInvite, setOpenInvite] = useState(false)
  const { teamId } = useParams()
  const [members, setMembers] = useState<TeamMember[]>([])
  const [invitations, setInvitations] = useState<TeamInvitation[]>([])
  const { loginUser } = useStore(loginUserStore)

  useEffect(() => {
    if (teamId) {
      loadMembers()
      loadPendingInvitations()
    }
  }, [teamId])

  const loadMembers = async () => {
    if (!teamId) return
    try {
      const { data } = await TeamService.getMembers(teamId)
      setMembers(data)
    } catch (error) {
      console.error(error)
    }
  }

  const loadPendingInvitations = async () => {
    if (!teamId) return
    try {
      const { data } = await TeamService.getPendingInvitations(teamId);
      setInvitations(data);
    } catch (error) {
      console.error(error)
    }
  }

  return (
    <LayoutBody className="flex flex-col space-y-8 p-8" fixedHeight>
      {/* Team Members Section */}
      <div className="space-y-4">
        <div className="flex items-center justify-between">
          <div className="space-y-1">
            <h2 className="text-xl font-semibold tracking-tight">Team Members</h2>
            <p className="text-sm text-muted-foreground">
              Here you can manage the members of your team.
            </p>
          </div>
          <Button onClick={() => setOpenInvite(true)}>
            <IconPlus className="mr-2 size-4" />
            Invite Members
          </Button>
        </div>

        <div className="flex items-center space-x-2">
          <Input
            placeholder="Search members"
            className="h-9 w-full"
          />
        </div>

        <div className="rounded-md border">
          <Table>
            <TableHeader>
              <TableRow>
                <TableHead className="w-[300px]">Name</TableHead>
                <TableHead>Email</TableHead>
                <TableHead>Role</TableHead>
                <TableHead className="text-right">Joined at</TableHead>
              </TableRow>
            </TableHeader>
            <TableBody>
              {members.map((member) => (
                <TableRow key={member.userId}>
                  <TableCell className="font-medium">
                    <div className="flex items-center space-x-3">
                      <Avatar className="size-8">
                        {member.avatar && (
                          <AvatarImage src={`data:image/png;base64,${member.avatar}`} />
                        )}
                        <AvatarFallback>{member.firstName?.[0]}{member.lastName?.[0]}</AvatarFallback>
                      </Avatar>
                      <div className="flex items-center gap-2">
                        <span>{member.userId}</span>
                        {member.userId === loginUser.username && (
                          <Badge variant="secondary" className="text-[10px] h-5 px-1.5">You</Badge>
                        )}
                      </div>
                    </div>
                  </TableCell>
                  <TableCell>{member.email}</TableCell>
                  <TableCell>
                    <div className="flex gap-2">
                      <Badge variant="outline" className="capitalize">{member.role}</Badge>
                      {/* Placeholder for Primary Owner if logic exists */}
                    </div>
                  </TableCell>
                  <TableCell className="text-right">
                    {member.joinedAt && <DateTime value={member.joinedAt} />}
                  </TableCell>
                </TableRow>
              ))}
            </TableBody>
          </Table>
        </div>
      </div>

      {/* Pending Invites Section */}
      <div className="space-y-4">
        <div className="space-y-1">
          <h2 className="text-xl font-semibold tracking-tight">Pending Invites</h2>
          <p className="text-sm text-muted-foreground">
            Here you can manage the pending invitations to your team.
          </p>
        </div>

        <div className="flex items-center space-x-2">
          <Input
            placeholder="Search Invitations"
            className="h-9 w-full"
          />
        </div>

        <div className="rounded-md border">
          <Table>
            <TableHeader>
              <TableRow>
                <TableHead>Email</TableHead>
                <TableHead>Role</TableHead>
                <TableHead>Invited at</TableHead>
                <TableHead>Expires at</TableHead>
                <TableHead className="text-right">Status</TableHead>
              </TableRow>
            </TableHeader>
            <TableBody>
              {invitations.length === 0 ? (
                <TableRow>
                  <TableCell colSpan={5} className="h-24 text-center">
                    No data available
                  </TableCell>
                </TableRow>
              ) : (
                invitations.map((invite) => (
                  <TableRow key={invite.id}>
                    <TableCell>{invite.email}</TableCell>
                    <TableCell>
                      <Badge variant="outline" className="capitalize">{invite.role}</Badge>
                    </TableCell>
                    <TableCell>
                      <DateTime value={invite.createdAt} />
                    </TableCell>
                    <TableCell>
                      <DateTime value={invite.expiresAt} />
                    </TableCell>
                    <TableCell className="text-right">
                      <Badge className="capitalize">{invite.status.toLowerCase()}</Badge>
                    </TableCell>
                  </TableRow>
                ))
              )}
            </TableBody>
          </Table>
        </div>
      </div>

      {teamId && (
        <InviteMembersDialog
          open={openInvite}
          onOpenChange={setOpenInvite}
          teamId={teamId}
        />
      )}
    </LayoutBody>
  )
}
