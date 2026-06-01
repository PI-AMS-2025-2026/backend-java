---
name: spec-driven
description: "Use when you need to turn an idea, request, or conversation into a structured feature specification with prioritized user stories, acceptance scenarios, requirements, edge cases, assumptions, and success criteria."
argument-hint: "Feature request or problem statement"
---

# Spec-Driven Feature Specification

Use this skill to convert a feature request into a reusable, reviewable specification before implementation.

## When To Use

- The user wants to start from a request and produce a feature spec.
- The work should follow a spec-driven workflow before coding.
- You need a clear definition of scope, user journeys, and success criteria.
- The request is ambiguous and needs targeted clarification before implementation.
- If the user provides no feature name or problem statement, ask them to describe the feature or problem they want to specify before proceeding.
- If the input is unrelated to software development or feature specification, politely say this skill is only for creating technical feature specifications and ask for a feature request.

## Inputs

- Feature name or short problem statement.
- Any existing conversation context, requirements, constraints, or examples.
- Optional branch name, if already chosen.
- Optional date, if the caller wants it embedded in the document.

## Workflow

1. Extract the feature goal from the conversation or input.
2. Identify the primary user journey first and order stories by priority.
3. Split the feature into independently testable slices.
4. Write acceptance scenarios using Given / When / Then language.
5. Capture edge cases, functional requirements, entities, success criteria, and assumptions.
6. If any clarification trigger is met, stop and ask only the minimum set of follow-up questions needed to unblock the spec.
7. Once clarification is resolved, produce the final specification in the template below.

## Decision Rules

- Prefer user outcomes over implementation details.
- Keep each user story independently testable.
- Assign P1 to the most valuable and smallest viable slice.
- Include data entities only when the feature actually manipulates data.
- Use measurable, technology-agnostic success criteria.
- After clarification has been resolved, annotate only minor residual uncertainties as NEEDS CLARIFICATION instead of guessing.

## Clarification Triggers

If any of these are missing, do not generate the specification yet; ask the minimum follow-up questions needed to unblock the spec:

- The target users or actors are unclear.
- The feature boundary is vague.
- A business rule affects priorities or acceptance behavior.
- The data model is required but not described.
- Success criteria cannot be measured from the request.

## Output Template

```markdown
# Feature Specification: [FEATURE NAME]

**Feature Branch**: `[###-feature-name]`

**Created**: [DATE]

**Status**: Draft

**Input**: User description: "$ARGUMENTS"

## User Scenarios & Testing *(mandatory)*

### User Story [N] - [Brief Title] (Priority: P1/P2/P3)

[Describe this user journey in plain language]

**Why this priority**: [Explain the value and why it has this priority level]

**Independent Test**: [Describe how this can be tested independently]

**Acceptance Scenarios**:

1. **Given** [initial state], **When** [action], **Then** [expected outcome]
2. **Given** [initial state], **When** [action], **Then** [expected outcome]

---

Repeat the block above for each additional user story as needed.

---

### Edge Cases

- What happens when [boundary condition]?
- How does system handle [error scenario]?

## Requirements *(mandatory)*

### Functional Requirements

- **FR-XXX**: System MUST [specific capability]
- **FR-XXX**: Users MUST be able to [key interaction]
- **FR-XXX**: System MUST [data requirement]
- **FR-XXX**: System MUST [behavior]
- Add as many FR entries as needed.

### Key Entities *(include if feature involves data)*

- **[Entity 1]**: [What it represents, key attributes without implementation]
- **[Entity 2]**: [What it represents, relationships to other entities]

## Success Criteria *(mandatory)*

### Measurable Outcomes

- **SC-001**: [Measurable metric]
- **SC-002**: [Measurable metric]
- **SC-003**: [User satisfaction or completion metric]
- **SC-004**: [Business metric]

## Assumptions

- [Assumption about target users]
- [Assumption about scope boundaries]
- [Assumption about data/environment]
- [Dependency on existing system/service]
```

## Completion Check

- The spec has a clear feature name and branch label.
- User stories are prioritized and independently testable.
- Acceptance scenarios are written in Given / When / Then form.
- Functional requirements are explicit and traceable.
- Edge cases, assumptions, and success criteria are present.
- Any unresolved point is marked NEEDS CLARIFICATION.

## Example Prompts

- "Crie a especificação spec-driven para cadastro de salas"
- "Transforme esta demanda em uma feature specification"
- "Escreva o spec-driven do fluxo de validação de horários"
