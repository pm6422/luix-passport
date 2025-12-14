import { useState, useEffect, useMemo, useCallback, useRef } from "react"
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
// batch selection removed
import { Avatar, AvatarFallback, AvatarImage } from "@/components/ui/avatar"
import { Badge } from "@/components/ui/badge"
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from "@/components/ui/select"
import { TeamService, TeamMember, TeamInvitation } from "@/services/team-service"
import { AccountService } from "@/services/account-service"
import { useLoginUserStore } from "@/stores/login-user-store"
import { toast } from "sonner"
import { RoleAny } from "@/components/custom/role/role-any"

export default function TeamMembers() {
  const [openInvite, setOpenInvite] = useState(false)
  const { teamId } = useParams()
  const [members, setMembers] = useState<TeamMember[]>([])
  const [invitations, setInvitations] = useState<TeamInvitation[]>([])
  const [memberQuery, setMemberQuery] = useState("")
  const [inviteQuery, setInviteQuery] = useState("")
  const { loginUser, hasRole } = useLoginUserStore()
  const avatarFileInputRef = useRef<HTMLInputElement>(null)

  const loadMembers = useCallback(async () => {
    if (!teamId) return
    try {
      const { data } = await TeamService.getMembers(teamId)
      setMembers(data)
    } catch (error) {
      console.error(error)
    }
  }, [teamId])

  const loadPendingInvitations = useCallback(async () => {
    if (!teamId) return
    try {
      const { data } = await TeamService.getPendingInvitations(teamId);
      setInvitations(data);
    } catch (error) {
      console.error(error)
    }
  }, [teamId])

  const filteredMembers = useMemo(() => {
    const q = memberQuery.trim().toLowerCase()
    return q
      ? members.filter((m) => {
        const values = [m.userId, m.email, m.firstName, m.lastName, m.role]
        return values.filter(Boolean).some((v) => String(v).toLowerCase().includes(q))
      })
      : members
  }, [members, memberQuery])


  const handleSelfAvatarClick = () => {
    avatarFileInputRef.current?.click()
  }

  const handleSelfAvatarFileChange = async (e: React.ChangeEvent<HTMLInputElement>) => {
    if (!e.target.files || e.target.files.length === 0) return
    const file = e.target.files[0]
    const formData = new FormData()
    formData.append("file", file)
    try {
      await AccountService.uploadAvatar(formData)
      toast.success("Avatar updated")
      await loadMembers()
    } catch (error) {
      toast.error("Avatar update failed")
      console.error(error)
    } finally {
      e.target.value = ""
    }
  }

  const getInviteProgress = (i: TeamInvitation) => {
    if (i.status === "ACCEPTED") return "Accepted"
    if (i.status === "REJECTED") return "Rejected"
    if (i.status === "EXPIRED") return "Expired"
    if (i.status === "REGISTERED") return "Registered"
    return "Unregistered"
  }

  const filteredInvitations = useMemo(() => {
    const q = inviteQuery.trim().toLowerCase()
    return q
      ? invitations.filter((i) => {
        const values = [i.memberEmail, i.memberRole, getInviteProgress(i)]
        return values.filter(Boolean).some((v) => String(v).toLowerCase().includes(q))
      })
      : invitations
  }, [invitations, inviteQuery])

  useEffect(() => {
    if (teamId) {
      loadMembers()
    }
  }, [teamId, loadMembers, loadPendingInvitations])

  useEffect(() => {
    if (teamId && hasRole("TEAM_OWNER")) {
      loadPendingInvitations()
    }
  }, [teamId, loadPendingInvitations, hasRole])

  // batch operations removed

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
          <RoleAny roleIds={["TEAM_OWNER"]}>
            <Button onClick={() => setOpenInvite(true)}>
              <IconPlus className="mr-2 size-4" />
              Invite Members
            </Button>
          </RoleAny>
        </div>

        <div className="flex items-center space-x-2">
          <Input
            placeholder="Search members"
            className="h-9 w-full"
            value={memberQuery}
            onChange={(e) => setMemberQuery(e.target.value)}
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
              {filteredMembers.map((member) => (
                <TableRow key={member.userId}>
                  <TableCell className="font-medium">
                    <div className="flex items-center space-x-3">
                      <div className="relative">
                        <Avatar
                          className={`size-8 ${member.userId === loginUser.username ? "cursor-pointer group" : ""}`}
                          onClick={member.userId === loginUser.username ? handleSelfAvatarClick : undefined}
                        >
                          {member.avatar && (
                            <AvatarImage src={`data:image/png;base64,${member.avatar}`} />
                          )}
                          <AvatarFallback>{member.firstName?.[0]}{member.lastName?.[0]}</AvatarFallback>
                        </Avatar>
                        {member.userId === loginUser.username && (
                          <input
                            ref={avatarFileInputRef}
                            type="file"
                            accept="image/*"
                            className="hidden"
                            onChange={handleSelfAvatarFileChange}
                          />
                        )}
                        {member.userId === loginUser.username && (
                          <span className="animate-pulse border-background absolute -end-0.5 -bottom-0.5 size-3 rounded-full border-2 bg-emerald-500">
                            <span className="sr-only">You</span>
                          </span>
                        )}
                      </div>
                      <span>{member.userId}</span>
                    </div>
                  </TableCell>
                  <TableCell>{member.email}</TableCell>
                  <TableCell>
                    <RoleAny roleIds={["TEAM_OWNER"]}>
                      <div className="flex items-center gap-2">
                        <Select
                          value={member.role}
                          onValueChange={async (value) => {
                            if (!teamId) return
                            try {
                              await TeamService.updateMemberRole(teamId, member.userId, value)
                              toast.success("Role updated")
                              await loadMembers()
                            } catch (error) {
                              toast.error("Role update failed")
                              console.error(error)
                            }
                          }}
                        >
                          <SelectTrigger size="sm" aria-label="Edit role" disabled={member.userId === loginUser.username}>
                            <SelectValue placeholder="Select role" />
                          </SelectTrigger>
                          <SelectContent>
                            <SelectItem value="TEAM_OWNER">TEAM_OWNER</SelectItem>
                            <SelectItem value="TEAM_MEMBER">TEAM_MEMBER</SelectItem>
                          </SelectContent>
                        </Select>
                      </div>
                    </RoleAny>
                    {!hasRole("TEAM_OWNER") && (
                      <div className="flex gap-2">
                        <Badge variant="outline" className="capitalize">{member.role}</Badge>
                      </div>
                    )}
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
      <RoleAny roleIds={["TEAM_OWNER"]}>
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
              value={inviteQuery}
              onChange={(e) => setInviteQuery(e.target.value)}
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
                  <TableHead>Progress</TableHead>
                  <TableHead className="text-right">Actions</TableHead>
                </TableRow>
              </TableHeader>
              <TableBody>
                {filteredInvitations.length === 0 ? (
                  <TableRow>
                    <TableCell colSpan={6} className="h-24 text-center">
                      No data available
                    </TableCell>
                  </TableRow>
                ) : (
                  filteredInvitations.map((invite) => (
                    <TableRow key={invite.id}>
                      <TableCell>{invite.memberEmail}</TableCell>
                      <TableCell>
                        <Badge variant="outline" className="capitalize">{invite.memberRole}</Badge>
                      </TableCell>
                      <TableCell>
                        <DateTime value={invite.createdAt} />
                      </TableCell>
                      <TableCell>
                        <DateTime value={invite.expiresAt} />
                      </TableCell>
                      <TableCell>
                        <Badge
                          className={`capitalize ${invite.status === "EXPIRED"
                            ? "bg-red-600/10 text-red-600 shadow-none"
                            : ""
                            }`}
                        >
                          {getInviteProgress(invite)}
                        </Badge>
                      </TableCell>
                      <TableCell className="text-right">
                        {invite.status === "EXPIRED" ? (
                          <Button
                            variant="outline"
                            size="sm"
                            onClick={async () => {
                              try {
                                await TeamService.resendInvitation(invite.id)
                                toast.success("Invitation resent")
                                loadPendingInvitations()
                              } catch (error) {
                                toast.error("Failed to resend invitation")
                                console.error(error)
                              }
                            }}
                          >
                            Resend
                          </Button>
                        ) : (
                          <span className="text-muted-foreground">—</span>
                        )}
                      </TableCell>
                    </TableRow>
                  ))
                )}
              </TableBody>
            </Table>
          </div>
        </div>
      </RoleAny>

      {teamId && (
        <RoleAny roleIds={["TEAM_OWNER"]}>
          <InviteMembersDialog
            open={openInvite}
            onOpenChange={setOpenInvite}
            teamId={teamId}
            onSuccess={() => {
              loadPendingInvitations()
            }}
          />
        </RoleAny>
      )}
    </LayoutBody>
  )
}
