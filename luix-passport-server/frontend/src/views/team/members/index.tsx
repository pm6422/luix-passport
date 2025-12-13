import { useState, useEffect, useMemo, useCallback } from "react"
import { Button } from "@/components/ui/button"
import { DateTime } from "@/components/custom/date-time"
import { InviteMembersDialog } from "./invite-dialog"
import { LayoutBody } from "@/layouts/layout-definitions"
import { IconPlus, IconTrash, IconRefresh } from "@tabler/icons-react"
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
import { Checkbox } from "@/components/ui/checkbox"
import { Avatar, AvatarFallback, AvatarImage } from "@/components/ui/avatar"
import { Badge } from "@/components/ui/badge"
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from "@/components/ui/select"
import { TeamService, TeamMember, TeamInvitation } from "@/services/team-service"
import { loginUserStore } from "@/stores/login-user-store"
import { useStore } from "exome/react"
import { toast } from "sonner"

export default function TeamMembers() {
  const [openInvite, setOpenInvite] = useState(false)
  const { teamId } = useParams()
  const [members, setMembers] = useState<TeamMember[]>([])
  const [invitations, setInvitations] = useState<TeamInvitation[]>([])
  const [memberQuery, setMemberQuery] = useState("")
  const [inviteQuery, setInviteQuery] = useState("")
  const [selectedInvitationIds, setSelectedInvitationIds] = useState<string[]>([])
  const { loginUser } = useStore(loginUserStore)

  

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

  const isOwner = useMemo(() => {
    const me = members.find(m => m.userId === loginUser.username)
    return me?.role === "OWNER"
  }, [members, loginUser.username])

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
          const values = [i.email, i.role, getInviteProgress(i)]
          return values.filter(Boolean).some((v) => String(v).toLowerCase().includes(q))
        })
      : invitations
  }, [invitations, inviteQuery])
  
  useEffect(() => {
    if (teamId) {
      loadMembers()
      loadPendingInvitations()
    }
  }, [teamId, loadMembers, loadPendingInvitations])

  const allSelected = useMemo(() => {
    const ids = filteredInvitations.map(i => i.id)
    return ids.length > 0 && ids.every(id => selectedInvitationIds.includes(id))
  }, [filteredInvitations, selectedInvitationIds])

  const toggleSelectAll = (checked: boolean) => {
    if (checked) {
      setSelectedInvitationIds(filteredInvitations.map(i => i.id))
    } else {
      setSelectedInvitationIds([])
    }
  }

  const toggleSelectOne = (id: string, checked: boolean) => {
    setSelectedInvitationIds(prev => {
      if (checked) {
        return Array.from(new Set([...prev, id]))
      } else {
        return prev.filter(v => v !== id)
      }
    })
  }

  const handleBatchResend = async () => {
    const expiredIds = selectedInvitationIds.filter(id => {
      const inv = invitations.find(i => i.id === id)
      return inv?.status === "EXPIRED"
    })
    if (expiredIds.length === 0) {
      toast.info("请选择状态为 Expired 的邀请进行重发")
      return
    }
    try {
      await TeamService.resendInvitations(expiredIds)
      toast.success(`已重发 ${expiredIds.length} 条邀请`)
      setSelectedInvitationIds([])
      await loadPendingInvitations()
    } catch (error) {
      toast.error("批量重发失败")
      console.error(error)
    }
  }

  const handleBatchDelete = async () => {
    if (selectedInvitationIds.length === 0) {
      toast.info("请先选择要删除的邀请")
      return
    }
    try {
      await TeamService.deleteInvitations(selectedInvitationIds)
      toast.success(`已删除 ${selectedInvitationIds.length} 条邀请`)
      setSelectedInvitationIds([])
      await loadPendingInvitations()
    } catch (error) {
      toast.error("批量删除失败")
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
                        <Avatar className="size-8">
                          {member.avatar && (
                            <AvatarImage src={`data:image/png;base64,${member.avatar}`} />
                          )}
                          <AvatarFallback>{member.firstName?.[0]}{member.lastName?.[0]}</AvatarFallback>
                        </Avatar>
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
                    {isOwner ? (
                      <div className="flex items-center gap-2">
                        <Select
                          value={member.role}
                          onValueChange={async (value) => {
                            if (!teamId) return
                            try {
                              await TeamService.updateMemberRole(teamId, member.userId, value)
                              toast.success("角色已更新")
                              await loadMembers()
                            } catch (error) {
                              toast.error("角色更新失败")
                              console.error(error)
                            }
                          }}
                        >
                          <SelectTrigger size="sm" aria-label="编辑角色" disabled={member.userId === loginUser.username}>
                            <SelectValue placeholder="选择角色" />
                          </SelectTrigger>
                          <SelectContent>
                            <SelectItem value="OWNER">OWNER</SelectItem>
                            <SelectItem value="MEMBER">MEMBER</SelectItem>
                          </SelectContent>
                        </Select>
                      </div>
                    ) : (
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
          {selectedInvitationIds.length > 0 && (
            <div className="flex items-center justify-between p-2">
              <div className="text-sm text-muted-foreground">
                已选择 {selectedInvitationIds.length} 条邀请
              </div>
              <div className="flex items-center gap-2">
                <Button variant="outline" size="sm" onClick={handleBatchResend}>
                  <IconRefresh className="size-4" />
                  重发邀请
                </Button>
                <Button variant="destructive" size="sm" onClick={handleBatchDelete}>
                  <IconTrash className="size-4" />
                  删除邀请
                </Button>
              </div>
            </div>
          )}
          <Table>
            <TableHeader>
              <TableRow>
                <TableHead className="w-8">
                  <Checkbox
                    checked={allSelected}
                    onCheckedChange={(v) => toggleSelectAll(Boolean(v))}
                    aria-label="Select all"
                  />
                </TableHead>
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
                  <TableCell colSpan={7} className="h-24 text-center">
                    No data available
                  </TableCell>
                </TableRow>
              ) : (
                filteredInvitations.map((invite) => (
                  <TableRow key={invite.id}>
                    <TableCell>
                      <Checkbox
                        checked={selectedInvitationIds.includes(invite.id)}
                        onCheckedChange={(v) => toggleSelectOne(invite.id, Boolean(v))}
                        aria-label="Select row"
                      />
                    </TableCell>
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
                    <TableCell>
                      <Badge
                        className={`capitalize ${
                          invite.status === "EXPIRED"
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

      {teamId && (
        <InviteMembersDialog
          open={openInvite}
          onOpenChange={setOpenInvite}
          teamId={teamId}
          onSuccess={() => {
            loadPendingInvitations()
          }}
        />
      )}
    </LayoutBody>
  )
}
