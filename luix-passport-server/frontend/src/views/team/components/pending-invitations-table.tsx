import { useState, useMemo, useEffect } from "react"
import { Card, CardHeader, CardTitle, CardContent } from "@/components/ui/card"
import { Input } from "@/components/ui/input"
import { Button } from "@/components/ui/button"
import { Badge } from "@/components/ui/badge"
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from "@/components/ui/table"
import { IconMail, IconLoader } from "@tabler/icons-react"
import { type TeamInvitation } from "@/domains/team"
import { UserDateTime } from "@/components/custom/display/user-date-time"
import { OverflowBadges } from "@/components/custom/display/overflow-badges"
import { TeamService } from "@/services/team-service"
import { toast } from "sonner"

interface PendingInvitationsTableProps {
  teamId: string
}

export function PendingInvitationsTable({ teamId }: PendingInvitationsTableProps) {
  const [inviteQuery, setInviteQuery] = useState("")
  const [invitations, setInvitations] = useState<TeamInvitation[]>([])

  useEffect(() => {
    const loadInvitations = async () => {
      try {
        const { data } = await TeamService.getPendingInvitations(teamId)
        setInvitations(data)
      } catch (_error) {
        console.error(_error)
      }
    }
    loadInvitations()
  }, [teamId])

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
        const values = [i.inviteeEmail, i.inviteeRoles, getInviteProgress(i)]
        return values.filter(Boolean).some((v) => String(v).toLowerCase().includes(q))
      })
      : invitations
  }, [invitations, inviteQuery])

  const handleResend = async (inviteId: string) => {
    try {
      await TeamService.resendInvitation(inviteId)
      toast.success("Invitation resent successfully")
      // Reload invitations data
      const { data } = await TeamService.getPendingInvitations(teamId)
      setInvitations(data)
    } catch (_error) {
      toast.error("Failed to resend invitation")
    }
  }

  return (
    <Card className="border-none shadow-sm bg-secondary/50 dark:bg-indigo-500/5 backdrop-blur-xl">
      <CardHeader className="pb-3 border-b border-white/5">
        <div className="flex items-center justify-between">
          <div>
            <CardTitle className="text-lg font-bold flex items-center gap-2">
              <IconMail className="size-5 text-indigo-600 dark:text-indigo-400" />
              Pending Invitations
            </CardTitle>
            <p className="text-xs text-muted-foreground mt-0.5">
              Manage pending team invitations.
            </p>
          </div>
          <div className="w-1/3 max-w-[220px]">
            <Input
              placeholder="Search invitations..."
              className="h-8 text-xs bg-background/30 border-indigo-500/10"
              value={inviteQuery}
              onChange={(e) => setInviteQuery(e.target.value)}
            />
          </div>
        </div>
      </CardHeader>
      <CardContent className="p-0">
        <Table>
          <TableHeader>
            <TableRow className="hover:bg-transparent border-indigo-500/10">
              <TableHead className="pl-6 h-10 uppercase text-[10px] font-bold tracking-wider text-muted-foreground/70">Recipient</TableHead>
              <TableHead className="h-10 uppercase text-[10px] font-bold tracking-wider text-muted-foreground/70">Roles</TableHead>
              <TableHead className="h-10 uppercase text-[10px] font-bold tracking-wider text-muted-foreground/70">Status</TableHead>
              <TableHead className="pr-6 h-10 text-right uppercase text-[10px] font-bold tracking-wider text-muted-foreground/70">Actions</TableHead>
            </TableRow>
          </TableHeader>
          <TableBody>
            {filteredInvitations.length === 0 ? (
              <TableRow>
                <TableCell colSpan={4} className="py-12 text-center text-muted-foreground italic text-sm">
                  No pending invitations found.
                </TableCell>
              </TableRow>
            ) : (
              filteredInvitations.map((invite) => (
                <TableRow key={invite.id} className="hover:bg-white/5 border-indigo-500/10">
                  <TableCell className="pl-6 py-4">
                    <span className="text-sm font-semibold">{invite.inviteeEmail}</span>
                  </TableCell>
                  <TableCell className="py-4">
                    <OverflowBadges
                      items={(invite.inviteeRoles || "").split(",").filter(Boolean)}
                      visibleCount={2}
                      badgeClassName="capitalize text-[10px] font-bold bg-indigo-500/5 text-indigo-600 border-indigo-500/10"
                      popoverBadgeClassName="capitalize"
                    />
                  </TableCell>
                  <TableCell className="py-4">
                    <div className="flex flex-col items-start gap-1.5">
                      <Badge
                        variant="secondary"
                        className={`text-[10px] font-bold px-2 py-0.5 uppercase tracking-wide rounded-full ${invite.status === "EXPIRED"
                          ? "bg-rose-500/10 text-rose-600 border-rose-500/20"
                          : "bg-emerald-500/10 text-emerald-600 border-emerald-500/20"
                          }`}
                      >
                        {invite.status !== "EXPIRED" && <IconLoader className="me-1 size-3 animate-spin" />}
                        {getInviteProgress(invite)}
                      </Badge>
                      <span className="text-[10px] text-muted-foreground flex items-center gap-1 opacity-80">
                        Expires at <UserDateTime value={invite.expiresAt} type="datetime" />
                      </span>
                    </div>
                  </TableCell>
                  <TableCell className="pr-6 py-4 text-right">
                    {invite.status === "EXPIRED" && (
                      <Button
                        variant="ghost"
                        size="sm"
                        className="h-7 text-[10px] font-bold text-indigo-600 hover:text-indigo-700 hover:bg-indigo-500/10"
                        onClick={() => handleResend(invite.id)}
                      >
                        RESEND
                      </Button>
                    )}
                  </TableCell>
                </TableRow>
              ))
            )}
          </TableBody>
        </Table>
      </CardContent>
    </Card>
  )
}
