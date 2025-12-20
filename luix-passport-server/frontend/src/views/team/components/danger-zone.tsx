import { useState } from "react"
import { useNavigate } from "react-router-dom"
import { Button } from "@/components/ui/button"
import { Card, CardHeader, CardTitle, CardContent } from "@/components/ui/card"
import { IconTrash } from "@tabler/icons-react"
import { ConfirmDialog } from "@/components/custom/dialog/confirm-dialog"
import { TeamService } from "@/services/team-service"
import { toast } from "sonner"

interface DangerZoneProps {
  teamId: string
}

export function DangerZone({ teamId }: DangerZoneProps) {
  const navigate = useNavigate()
  const [deleteOpen, setDeleteOpen] = useState(false)

  const handleDeleteTeam = async () => {
    try {
      await TeamService.delete(teamId)
      toast.success("Team deleted")
      navigate("/console")
      setTimeout(() => window.location.reload(), 100)
    } catch (error) {
      toast.error("Failed to delete team")
      console.error(error)
    }
  }

  return (
    <>
      <Card className="border-none shadow-sm shadow-rose-500/10 bg-secondary/50 dark:bg-rose-500/5 backdrop-blur-xl">
        <CardHeader className="pb-3 border-b border-rose-500/10">
          <CardTitle className="text-sm font-bold text-rose-600 dark:text-rose-400 flex items-center gap-2">
            <IconTrash className="size-4" />
            Danger Zone
          </CardTitle>
        </CardHeader>
        <CardContent className="pt-4">
          <p className="text-[10px] font-medium text-rose-600/70 mb-4 leading-relaxed italic">
            Deleting this identity organization will dissolve the organization and remove all member associations and invitations. Member user accounts and their personal data will remain unaffected.
          </p>
          <Button variant="destructive" className="w-full text-xs font-bold shadow-lg shadow-rose-500/20" onClick={() => setDeleteOpen(true)}>
            DELETE TEAM
          </Button>
        </CardContent>
      </Card>

      <ConfirmDialog
        open={deleteOpen}
        onOpenChange={setDeleteOpen}
        title="Delete Team"
        description="This action cannot be undone. This will permanently delete the team and remove all member associations."
        onConfirm={handleDeleteTeam}
        confirmText="Delete Team"
      />
    </>
  )
}
