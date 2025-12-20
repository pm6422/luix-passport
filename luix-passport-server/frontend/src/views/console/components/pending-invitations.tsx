import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card"
import { Badge } from "@/components/ui/badge"
import { Button } from "@/components/ui/button"
import { Mail } from "lucide-react"
import { AnimatePresence, motion } from "framer-motion"
import { type TeamInvitation } from "@/domains/team"
import { TeamService } from "@/services/team-service"
import { useNavigate } from "react-router-dom"
import { useEffect, useState } from "react"

export function PendingInvitations() {
  const navigate = useNavigate()
  const [invitations, setInvitations] = useState<TeamInvitation[]>([])

  useEffect(() => {
    const fetchInvitations = async () => {
      try {
        const res = await TeamService.getUserInvitations()
        setInvitations(res.data)
      } catch (error) {
        console.error("Failed to load invitations:", error)
      }
    }
    fetchInvitations()
  }, [])

  if (invitations.length === 0) {
    return null
  }

  return (
    <AnimatePresence>
      <motion.div
        initial={{ opacity: 0, scale: 0.95 }}
        animate={{ opacity: 1, scale: 1 }}
        exit={{ opacity: 0, scale: 0.95 }}
      >
        <Card className="border border-border/50 shadow-lg shadow-primary/10 bg-card">
          <CardHeader className="pb-3 border-b border-border/50">
            <div className="flex items-center justify-between">
              <CardTitle className="text-md">Team Requests</CardTitle>
              <Badge variant="destructive" className="rounded-full h-5 w-5 p-0 flex items-center justify-center text-[10px]">
                {invitations.length}
              </Badge>
            </div>
          </CardHeader>
          <CardContent className="p-4 space-y-4">
            {invitations.map((inv: TeamInvitation) => (
              <div key={inv.id} className="p-3 bg-muted/30 rounded-lg flex flex-col gap-3">
                <div className="flex items-start gap-3">
                  <div className="h-8 w-8 rounded-full bg-blue-500/10 flex items-center justify-center">
                    <Mail className="h-4 w-4 text-blue-600" />
                  </div>
                  <div className="flex-1 overflow-hidden">
                    <p className="text-xs font-semibold truncate italic text-muted-foreground">{inv.inviteeRoles}</p>
                    <p className="text-[10px] text-muted-foreground">Expires {new Date(inv.expiresAt).toLocaleDateString()}</p>
                  </div>
                </div>
                <div className="flex gap-2">
                  <Button size="sm" className="flex-1 h-7 text-[10px]" onClick={() => navigate(`/teams`)}>Accept</Button>
                  <Button size="sm" variant="ghost" className="flex-1 h-7 text-[10px]">Ignore</Button>
                </div>
              </div>
            ))}
          </CardContent>
        </Card>
      </motion.div>
    </AnimatePresence>
  )
}
