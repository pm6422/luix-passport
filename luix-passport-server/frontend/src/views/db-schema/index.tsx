import { LayoutBody } from "@/layouts/layout-definitions"
import { Card } from "@/components/ui/card"
import { RoleDeveloper } from "@/components/custom/role"

export default function DbSchema() {
  return (
    <RoleDeveloper>
      <LayoutBody className="flex flex-col" fixedHeight>
        <Card className="py-0">
          <iframe
            src="https://dbdocs.io/pm6422/luix-passport"
            className="rounded-lg"
            width="100%"
            height="900">
          </iframe>
        </Card>
      </LayoutBody>
    </RoleDeveloper>
  )
}
