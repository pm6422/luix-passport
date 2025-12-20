import { useState, useMemo } from "react"
import { Card, CardHeader, CardTitle, CardContent } from "@/components/ui/card"
import { Input } from "@/components/ui/input"
import { Badge } from "@/components/ui/badge"
import { Avatar, AvatarFallback, AvatarImage } from "@/components/ui/avatar"
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from "@/components/ui/table"
import { IconUsers } from "@tabler/icons-react"
import { type TeamMember } from "@/domains/team"
import { UserDateTime } from "@/components/custom/display/user-date-time"
import { TeamRoleSelector } from "@/components/custom/form-field/team-role-selector"
import { RoleTeamOwner } from "@/layouts/permission/role-control"
import { useLoginUserStore } from "@/stores/login-user-store"
import { ROLE_TEAM_OWNER } from "@/domains/role"
import { ConfirmDialog } from "@/components/custom/dialog/confirm-dialog"
import { TeamService } from "@/services/team-service"
import { toast } from "sonner"

interface TeamMembersTableProps {
  teamId: string
  members: TeamMember[]
  allRoles: string[]
  onMemberUpdated: () => void
}

export function TeamMembersTable({ teamId, members, allRoles, onMemberUpdated }: TeamMembersTableProps) {
  const { loginUser, hasRole } = useLoginUserStore()
  const [memberQuery, setMemberQuery] = useState("")
  const [roleConfirmOpen, setRoleConfirmOpen] = useState(false)
  const [pendingRoleChange, setPendingRoleChange] = useState<{ userId: string, newRoles: string[] } | null>(null)

  const filteredMembers = useMemo(() => {
    const q = memberQuery.trim().toLowerCase()
    return q
      ? members.filter((m) => {
        const values = [m.userId, m.email, m.firstName, m.lastName, m.role]
        return values.filter(Boolean).some((v) => String(v).toLowerCase().includes(q))
      })
      : members
  }, [members, memberQuery])

  const handleRoleChange = (userId: string, newRoles: string[]) => {
    setPendingRoleChange({ userId, newRoles })
    setRoleConfirmOpen(true)
  }

  const confirmRoleChange = async () => {
    if (!pendingRoleChange) return
    try {
      await TeamService.updateMemberRole(teamId, pendingRoleChange.userId, pendingRoleChange.newRoles)
      toast.success("Role updated")
      onMemberUpdated()
    } catch (error) {
      toast.error("Role update failed")
      console.error(error)
    } finally {
      setRoleConfirmOpen(false)
      setPendingRoleChange(null)
    }
  }

  return (
    <>
      <Card className="border-none shadow-sm bg-secondary/50 dark:bg-sky-500/5 backdrop-blur-xl">
        <CardHeader className="pb-3 border-b border-white/5">
          <div className="flex items-center justify-between">
            <div>
              <CardTitle className="text-lg font-bold flex items-center gap-2">
                <IconUsers className="size-5 text-sky-600 dark:text-sky-400" />
                Team Members
              </CardTitle>
              <p className="text-xs text-muted-foreground mt-0.5">
                Manage the members of your team.
              </p>
            </div>
            <div className="w-1/3 max-w-[220px]">
              <Input
                placeholder="Search members..."
                className="h-8 text-xs bg-background/30 border-sky-500/10"
                value={memberQuery}
                onChange={(e) => setMemberQuery(e.target.value)}
              />
            </div>
          </div>
        </CardHeader>
        <CardContent className="p-0">
          <Table>
            <TableHeader>
              <TableRow className="hover:bg-transparent border-sky-500/10">
                <TableHead className="pl-6 h-10 uppercase text-[10px] font-bold tracking-wider text-muted-foreground/70">Member</TableHead>
                <TableHead className="h-10 uppercase text-[10px] font-bold tracking-wider text-muted-foreground/70">Email</TableHead>
                <TableHead className="h-10 uppercase text-[10px] font-bold tracking-wider text-muted-foreground/70">Authorized Roles</TableHead>
                <TableHead className="pr-6 h-10 text-right uppercase text-[10px] font-bold tracking-wider text-muted-foreground/70">Joined at</TableHead>
              </TableRow>
            </TableHeader>
            <TableBody>
              {filteredMembers.map((member) => (
                <TableRow key={member.userId} className="group hover:bg-white/5 border-sky-500/10">
                  <TableCell className="pl-6 py-4">
                    <div className="flex items-center gap-3">
                      <div className="relative">
                        <Avatar className="size-9 border border-sky-500/20 shadow-sm">
                          {member.avatar && (
                            <AvatarImage src={`data:image/png;base64,${member.avatar}`} />
                          )}
                          <AvatarFallback className="bg-sky-500/10 text-sky-600 font-bold">{member.firstName?.[0] || member.userId?.[0]?.toUpperCase()}</AvatarFallback>
                        </Avatar>
                        {member.userId === loginUser.username && (
                          <span className="absolute -right-0.5 -bottom-0.5 shadow-sm border-2 border-background flex h-3 w-3 bg-emerald-500 rounded-full" />
                        )}
                      </div>
                      <span className="text-sm font-semibold text-foreground/90">{member.userId}</span>
                    </div>
                  </TableCell>
                  <TableCell className="py-4 text-xs font-medium text-muted-foreground">{member.email}</TableCell>
                  <TableCell className="py-4">
                    <RoleTeamOwner>
                      <TeamRoleSelector
                        value={member.roles || [member.role]}
                        allRoles={allRoles}
                        disabled={member.userId === loginUser.username}
                        onChange={(newRoles) => handleRoleChange(member.userId, newRoles)}
                        className="h-8 max-w-[180px] bg-background/50 border-sky-500/20"
                      />
                    </RoleTeamOwner>
                    {!hasRole(ROLE_TEAM_OWNER) && (
                      <div className="flex flex-wrap gap-1.5">
                        {(member.roles || [member.role]).map(r => (
                          <Badge key={r} variant="outline" className="capitalize text-[10px] font-bold bg-sky-500/5 text-sky-600 border-sky-500/20">{r}</Badge>
                        ))}
                      </div>
                    )}
                  </TableCell>
                  <TableCell className="pr-6 py-4 text-right">
                    {member.joinedAt && <UserDateTime value={member.joinedAt} twoLines={true} align="end" className="text-[10px] font-medium opacity-80" />}
                  </TableCell>
                </TableRow>
              ))}
              {filteredMembers.length === 0 && (
                <TableRow>
                  <TableCell colSpan={4} className="py-12 text-center text-muted-foreground italic text-sm">
                    No team members found.
                  </TableCell>
                </TableRow>
              )}
            </TableBody>
          </Table>
        </CardContent>
      </Card>

      <ConfirmDialog
        open={roleConfirmOpen}
        onOpenChange={setRoleConfirmOpen}
        title="Change Role"
        description="Are you sure you want to change the role for this member?"
        onConfirm={confirmRoleChange}
        onCancel={() => setPendingRoleChange(null)}
      />
    </>
  )
}
