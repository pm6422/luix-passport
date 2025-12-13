import { useState } from "react"
import { Button } from "@/components/ui/button"
import { InviteMembersDialog } from "./invite-dialog"
import { LayoutBody } from "@/layouts/layout-definitions"
import { IconUserPlus } from "@tabler/icons-react"
import { useParams } from "react-router-dom"

export default function TeamMembers() {
  const [openInvite, setOpenInvite] = useState(false)
  const { teamId } = useParams()

  return (
    <LayoutBody className="flex flex-col" fixedHeight>
      <div className="flex items-center justify-between p-4 border-b bg-background">
        <h1 className="text-2xl font-bold tracking-tight">Team Members</h1>
        <Button onClick={() => setOpenInvite(true)}>
          <IconUserPlus className="mr-2 size-4" />
          Invite Members
        </Button>
      </div>

      <div className="flex-1 p-4">
        <div className="border rounded-lg p-16 text-center text-muted-foreground bg-muted/10 border-dashed">
          <p>Member list will be displayed here.</p>
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
